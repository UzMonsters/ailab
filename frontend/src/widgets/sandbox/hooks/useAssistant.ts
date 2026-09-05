import { useState, useCallback, useEffect, useMemo } from 'react';
import { AssistantTools } from '../../../engine/assistant/AssistantTools';
import type { Engine } from '../../../engine';
import type { EquipmentRegistry } from '../../../engine';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';
import { errorMessage } from '@/shared/utils/errorMessage';
import { useAuthStore } from '@/stores/auth.store';

export type AssistantMessage = {
  role: 'user' | 'assistant';
  content: string;
  actions?: { label: string; action: string; payload: Record<string, unknown> }[];
};

export function useAssistant(engine: Engine | null, registry: EquipmentRegistry, workspaceId?: string | null) {
  const currentUserId = useAuthStore((state) => state.user?.id);
  const [messages, setMessages] = useState<AssistantMessage[]>([
    {
      role: 'assistant',
      content: 'Привет! Я лабораторный ассистент. Могу проверить установку, подсказать следующий шаг или объяснить результат.',
    }
  ]);
  const tools = useMemo(() => engine ? new AssistantTools(engine, registry) : null, [engine, registry]);

  useEffect(() => {
    if (!workspaceId) return;
    let active = true;
    void workspaceCollaborationApi.chat(workspaceId).then((page) => {
      if (!active) return;
      setMessages(page.items.map((message) => ({
        role: message.author.id === currentUserId ? 'user' : 'assistant',
        content: message.deletedAt ? 'Сообщение удалено' : message.body,
      })));
      const last = page.items.at(-1);
      if (last) void workspaceCollaborationApi.markChatRead(workspaceId, last.id);
    }).catch((reason) => {
      if (active) setMessages([{ role: 'assistant', content: errorMessage(reason, 'Не удалось загрузить командный чат') }]);
    });
    return () => { active = false; };
  }, [currentUserId, workspaceId]);

  const sendMessage = useCallback((text: string) => {
    if (!text.trim()) return;

    if (workspaceId) {
      setMessages(prev => [...prev, { role: 'user', content: text }]);
      void workspaceCollaborationApi.sendMessage(workspaceId, text).catch((reason) => {
        setMessages(prev => [...prev, { role: 'assistant', content: errorMessage(reason, 'Сообщение не отправлено') }]);
      });
      return;
    }
    
    setMessages(prev => [...prev, { role: 'user', content: text }]);
    
    // Local deterministic helper. Persisted team chat is used for saved workspaces.
    setTimeout(() => {
      const lowerText = text.toLowerCase();
      let response: AssistantMessage = { role: 'assistant', content: "Уточните вопрос — например: «что в лаборатории?», «добавь воду» или «помоги»." };

      if (lowerText.includes("help") || lowerText.includes("помог")) {
        response = {
          role: 'assistant',
          content: 'Я могу проанализировать сцену и предложить безопасное следующее действие.',
          actions: [
            { label: 'Что сейчас в лаборатории?', action: 'analyze_lab', payload: {} }
          ]
        };
      } else if (lowerText.includes("what's in the lab") || lowerText.includes("analyze_lab") || lowerText.includes("что в лаборатории") || lowerText.includes("что сейчас")) {
        if (tools) {
          const state = tools.getLabState();
          response = {
            role: 'assistant',
            content: `На рабочем поле ${state.objects.length} приборов: ${state.objects.map(o => o.type).join(', ') || 'пока пусто'}.`
          };
        }
      } else if (lowerText.includes("add water") || lowerText.includes("добавь воду") || lowerText.includes("налей воду")) {
        response = {
          role: 'assistant',
          content: 'Выберите сосуд, в который добавить 25 мл воды.',
          actions: tools?.getLabState().objects.filter(o => o.capabilities?.container).map(o => ({
            label: `Добавить в ${o.type} (${o.id.slice(0, 4)})`,
            action: 'add_material',
            payload: { id: o.id, material: { materialId: 'COMP-H2O', name: 'Water', state: 'liquid', amount: 25 } }
          }))
        };
      } else {
        response = {
           role: 'assistant',
           content: 'Могу добавить воду, проверить приборы или рассказать, что находится на сцене.'
        };
      }

      setMessages(prev => [...prev, response]);
    }, 500);
  }, [tools, workspaceId]);

  const executeAction = useCallback((action: string, payload: Record<string, unknown>) => {
    if (action === 'analyze_lab') {
      sendMessage("What's in the lab?");
      return;
    }
    
    if (tools) {
      const result = tools.executeAction(action, payload);
      if (result.success) {
        setMessages(prev => [...prev, { role: 'assistant', content: `Готово — действие выполнено.` }]);
      } else {
        setMessages(prev => [...prev, { role: 'assistant', content: `Не удалось выполнить действие: ${result.error}` }]);
      }
    }
  }, [tools, sendMessage]);

  return { messages, sendMessage, executeAction, isTeamChat: Boolean(workspaceId) };
}
