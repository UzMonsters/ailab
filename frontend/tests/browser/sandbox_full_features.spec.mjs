import { test, expect } from '@playwright/test';

async function mockBackend(page) {
  await page.route('**/api/v1/users/me', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ id: 'test-user', username: 'tester', email: 'tester@test.com', role: 'ROLE_USER' })
  }));
  await page.route('**/api/v1/workspaces/full-test/state', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({
      workspaceId: 'full-test', stateVersion: 1, sessionId: null,
      viewport: { position: { x: 0, y: 0 }, zoom: 1 }, grid: { enabled: true }, log: [], updatedAt: new Date().toISOString(),
      items: [], connections: [],
    })
  }));
  await page.route('**/api/v1/workspaces/full-test/events', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ clientEventId: 'test', eventId: 'event-1', eventType: 'ITEM_ADDED', workspaceId: 'full-test', stateVersion: 2, stateDelta: {}, safetyWarnings: [], occurredAt: new Date().toISOString() })
  }));
  await page.route('**/api/v1/workspaces/full-test/autosave**', (route) => route.fulfill({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify({ stateVersion: 2, savedAt: new Date().toISOString() })
  }));
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
    window.__emitWorkspaceEvent = (event) => MockWebSocket.instances.at(-1)?.onmessage?.({ data: `MESSAGE\ndestination:/topic/workspaces/full-test\n\n${JSON.stringify(event)}\0` });
  });
});

test('Full Sandbox Features & Equipment Testing', async ({ page }) => {
  await mockBackend(page);
  await page.goto('/ru/workspace/sandbox?workspace=full-test');

  // 1. Wait for Sandbox to load
  await expect(page.getByTestId('sandbox-sync-status')).toContainText(/Ready|Готово/, { timeout: 20_000 });

  // 2. Click Equipment items to place them on canvas
  const hotplateCard = page.getByText('Hotplate').first();
  if (await hotplateCard.isVisible()) {
    await hotplateCard.click();
  }

  const thermometerCard = page.getByText('Thermometer').first();
  if (await thermometerCard.isVisible()) {
    await thermometerCard.click();
  }

  // 3. Switch to Materials tab in Library
  const materialsTab = page.getByRole('button', { name: /Вещества|Materials/i }).first();
  if (await materialsTab.isVisible()) {
    await materialsTab.click();
  }

  // 4. Click Dock tabs
  const measurementsTab = page.getByRole('button', { name: /Измерения|Measurements/i }).first();
  if (await measurementsTab.isVisible()) {
    await measurementsTab.click();
  }

  const chartsTab = page.getByRole('button', { name: /Графики|Charts/i }).first();
  if (await chartsTab.isVisible()) {
    await chartsTab.click();
  }

  const eventsTab = page.getByRole('button', { name: /События|Events/i }).first();
  if (await eventsTab.isVisible()) {
    await eventsTab.click();
  }

  // 5. Take screenshot of canvas with items and materials
  await page.screenshot({ path: 'sandbox-full-test.png', fullPage: true });
});
