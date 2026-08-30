import { useState, useCallback } from 'react';
import { AssistantTools } from '../../../engine/assistant/AssistantTools';
import type { Engine } from '../../../engine';
import type { EquipmentRegistry } from '../../../engine';

export type AssistantMessage = {
  role: 'user' | 'assistant';
  content: string;
  actions?: { label: string; action: string; payload: Record<string, unknown> }[];
};

export function useAssistant(engine: Engine | null, registry: EquipmentRegistry) {
  const [messages, setMessages] = useState<AssistantMessage[]>([
    {
      role: 'assistant',
      content: 'Hello! I am your AI Lab Assistant. How can I help you with your experiment today?',
    }
  ]);
  const [tools] = useState(() => engine ? new AssistantTools(engine, registry) : null);

  const sendMessage = useCallback((text: string) => {
    if (!text.trim()) return;
    
    setMessages(prev => [...prev, { role: 'user', content: text }]);
    
    // Mock AI response
    setTimeout(() => {
      const lowerText = text.toLowerCase();
      let response: AssistantMessage = { role: 'assistant', content: "I'm not sure how to help with that." };

      if (lowerText.includes("help")) {
        response = {
          role: 'assistant',
          content: 'I can help you analyze the lab state, or suggest actions.',
          actions: [
            { label: 'What is in the lab?', action: 'analyze_lab', payload: {} }
          ]
        };
      } else if (lowerText.includes("what's in the lab") || lowerText.includes("analyze_lab")) {
        if (tools) {
          const state = tools.getLabState();
          response = {
            role: 'assistant',
            content: `There are ${state.objects.length} objects in the lab: ${state.objects.map(o => o.type).join(', ')}.`
          };
        }
      } else if (lowerText.includes("add water")) {
        response = {
          role: 'assistant',
          content: 'I can add water. Which vessel should I add it to?',
          actions: tools?.getLabState().objects.filter(o => o.capabilities?.container).map(o => ({
            label: `Add to ${o.type} (${o.id.slice(0, 4)})`,
            action: 'add_material',
            payload: { id: o.id, material: { materialId: 'COMP-H2O', name: 'Water', state: 'liquid', amount: 25 } }
          }))
        };
      } else {
        response = {
           role: 'assistant',
           content: 'I can help you add water, or tell you what is in the lab.'
        };
      }

      setMessages(prev => [...prev, response]);
    }, 500);
  }, [tools]);

  const executeAction = useCallback((action: string, payload: Record<string, unknown>) => {
    if (action === 'analyze_lab') {
      sendMessage("What's in the lab?");
      return;
    }
    
    if (tools) {
      const result = tools.executeAction(action, payload);
      if (result.success) {
        setMessages(prev => [...prev, { role: 'assistant', content: `Action executed successfully.` }]);
      } else {
        setMessages(prev => [...prev, { role: 'assistant', content: `Action failed: ${result.error}` }]);
      }
    }
  }, [tools, sendMessage]);

  return { messages, sendMessage, executeAction };
}
