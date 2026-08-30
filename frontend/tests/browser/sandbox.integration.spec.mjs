import { test, expect } from '@playwright/test';

const state = (version = 1) => ({
  workspaceId: 'browser-test', stateVersion: version, sessionId: null,
  viewport: { position: { x: 0, y: 0 }, zoom: 1 }, grid: { enabled: true }, log: [], updatedAt: new Date().toISOString(),
  items: [], connections: [],
});

async function mockBackend(page, options = {}) {
  let eventAttempts = 0;
  await page.route('**/api/v1/users/me', (route) => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ id: 'test-user', username: 'browser', email: 'browser@example.test', role: 'ROLE_USER' }) }));
  await page.route('**/api/v1/workspaces/browser-test/state', (route) => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(state(options.stateVersion ?? 1)) }));
  await page.route('**/api/v1/workspaces/browser-test/events', async (route) => {
    eventAttempts += 1;
    if (options.conflict && eventAttempts === 1) return route.fulfill({ status: 409, contentType: 'application/json', body: JSON.stringify({ message: 'Version conflict' }) });
    return route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ clientEventId: 'test', eventId: `event-${eventAttempts}`, eventType: 'ITEM_ADDED', workspaceId: 'browser-test', stateVersion: eventAttempts + 1, stateDelta: {}, safetyWarnings: [], occurredAt: new Date().toISOString() }) });
  });
  await page.route('**/api/v1/workspaces/browser-test/autosave**', (route) => route.fulfill({ status: options.conflict ? 409 : 200, contentType: 'application/json', body: JSON.stringify(options.conflict ? { message: 'Version conflict' } : { stateVersion: 2, savedAt: new Date().toISOString() }) }));
}

test.beforeEach(async ({ page }) => {
  await page.addInitScript(() => {
    class MockWebSocket {
      static instances = [];
      readyState = 0;
      constructor() { MockWebSocket.instances.push(this); setTimeout(() => { this.readyState = 1; this.onopen?.(); }, 0); }
      send() {}
      close() { this.readyState = 3; this.onclose?.(); }
    }
    window.WebSocket = MockWebSocket;
    window.__emitWorkspaceEvent = (event) => MockWebSocket.instances.at(-1)?.onmessage?.({ data: `MESSAGE\ndestination:/topic/workspaces/browser-test\n\n${JSON.stringify(event)}\0` });
  });
});

test('hydrates before applying realtime events and reconciles a version gap', async ({ page }) => {
  await mockBackend(page, { stateVersion: 2 });
  await page.goto('/en/workspace/sandbox?workspace=browser-test');
  await expect(page.getByTestId('sandbox-sync-status')).toContainText('Ready', { timeout: 15_000 });
  await page.evaluate(() => window.__emitWorkspaceEvent({ stateVersion: 4, stateDelta: {} }));
  await expect(page.getByTestId('sandbox-sync-status')).toContainText(/Ready|Reconciling/);
});

test('keeps pending events in durable storage when backend returns conflict', async ({ page }) => {
  await mockBackend(page, { conflict: true });
  await page.goto('/en/workspace/sandbox?workspace=browser-test');
  await expect(page.getByTestId('sandbox-sync-status')).toContainText('Ready', { timeout: 15_000 });
  await page.evaluate(() => localStorage.setItem('ailab_pending_events_browser-test', JSON.stringify([{ clientEventId: 'queued', eventType: 'ITEM_ADDED', payload: {} }])));
  await page.reload();
  await expect(page.getByTestId('sandbox-sync-status')).toContainText(/Conflict|Offline queue|Ready/, { timeout: 15_000 });
  await expect.poll(() => page.evaluate(() => JSON.parse(localStorage.getItem('ailab_pending_events_browser-test') ?? '[]').length)).toBe(1);
});

test('reconnects realtime after the browser comes back online', async ({ page }) => {
  await mockBackend(page);
  await page.goto('/en/workspace/sandbox?workspace=browser-test');
  await expect(page.getByTestId('sandbox-sync-status')).toContainText('Ready', { timeout: 15_000 });
  await page.evaluate(() => window.dispatchEvent(new Event('offline')));
  await page.evaluate(() => window.dispatchEvent(new Event('online')));
  await expect(page.getByTestId('sandbox-sync-status')).toContainText(/Ready|Saved/, { timeout: 5_000 });
});
