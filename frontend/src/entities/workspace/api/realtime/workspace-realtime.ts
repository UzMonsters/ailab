import { getAccessToken, getApiBaseUrl } from '@/shared/api/client';

export interface WorkspaceRealtimeHandlers {
  onWorkspaceEvent?: (event: unknown) => void;
  onExperimentEvent?: (event: unknown) => void;
  onAck?: (event: unknown) => void;
  onError?: (event: unknown) => void;
}

export interface WorkspaceRealtimeConnection {
  sendWorkspaceEvent: (event: unknown) => void;
  sendExperimentCommand: (command: unknown) => void;
  close: () => void;
}

function frame(command: string, headers: Record<string, string> = {}, body = '') {
  const headerText = Object.entries(headers).map(([key, value]) => `${key}:${value}`).join('\n');
  return `${command}\n${headerText}\n\n${body}\0`;
}

function parseFrame(raw: string): { command: string; headers: Record<string, string>; body: unknown } | null {
  const value = raw.replace(/^\n+/, '');
  const separator = value.indexOf('\n\n');
  if (separator < 0) return null;
  const lines = value.slice(0, separator).split('\n');
  const headers: Record<string, string> = {};
  for (const line of lines.slice(1)) {
    const index = line.indexOf(':');
    if (index > 0) headers[line.slice(0, index)] = line.slice(index + 1);
  }
  const bodyText = value.slice(separator + 2).replace(/\0$/, '');
  let body: unknown = bodyText;
  try { body = bodyText ? JSON.parse(bodyText) : null; } catch { /* keep text */ }
  return { command: lines[0], headers, body };
}

export function connectWorkspaceRealtime(workspaceId: string, sessionId: string | null, handlers: WorkspaceRealtimeHandlers): WorkspaceRealtimeConnection {
  const noop: WorkspaceRealtimeConnection = { sendWorkspaceEvent: () => undefined, sendExperimentCommand: () => undefined, close: () => undefined };
  if (typeof window === 'undefined') return noop;
  const token = getAccessToken();
  if (!token) return noop;

  const apiUrl = getApiBaseUrl();
  const socketUrl = apiUrl.replace(/^http:/, 'ws:').replace(/^https:/, 'wss:') + '/ws';
  const socket = new WebSocket(socketUrl);
  let buffer = '';
  let connected = false;

  const subscribe = (id: string, destination: string) => socket.send(frame('SUBSCRIBE', { id, destination, ack: 'auto' }));

  socket.onopen = () => socket.send(frame('CONNECT', {
    'accept-version': '1.2',
    host: new URL(apiUrl).host,
    authorization: `Bearer ${token}`,
    'heart-beat': '10000,10000',
  }));
  socket.onmessage = (message) => {
    buffer += String(message.data);
    const frames = buffer.split('\0');
    buffer = frames.pop() || '';
    for (const raw of frames) {
      const parsed = parseFrame(raw);
      if (!parsed) continue;
      if (parsed.command === 'CONNECTED') {
        connected = true;
        subscribe('workspace', `/topic/workspaces/${workspaceId}`);
        subscribe('acks', '/user/queue/acks');
        subscribe('errors', '/user/queue/errors');
        if (sessionId) subscribe('experiment', `/topic/experiments/${sessionId}`);
      } else if (parsed.command === 'MESSAGE') {
        const destination = parsed.headers.destination || '';
        if (destination.includes('/queue/acks')) handlers.onAck?.(parsed.body);
        else if (destination.includes('/queue/errors')) handlers.onError?.(parsed.body);
        else if (destination.includes('/experiments/')) handlers.onExperimentEvent?.(parsed.body);
        else handlers.onWorkspaceEvent?.(parsed.body);
      } else if (parsed.command === 'ERROR') {
        handlers.onError?.(parsed.body);
      }
    }
  };
  socket.onerror = () => handlers.onError?.({ message: 'Realtime connection failed' });

  return {
    sendWorkspaceEvent: (event: unknown) => {
      if (connected) socket.send(frame('SEND', { destination: `/app/workspaces/${workspaceId}/events`, 'content-type': 'application/json' }, JSON.stringify(event)));
    },
    sendExperimentCommand: (command: unknown) => {
      if (connected && sessionId) socket.send(frame('SEND', { destination: `/app/experiments/${sessionId}/commands`, 'content-type': 'application/json' }, JSON.stringify(command)));
    },
    close: () => {
      if (socket.readyState === WebSocket.OPEN) socket.send(frame('DISCONNECT', { receipt: 'close' }));
      socket.close();
    },
  };
}
