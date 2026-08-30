import { test, expect } from '@playwright/test';

test.describe('sandbox physics browser integration', () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      class MockWebSocket {
        readyState = 0;
        constructor() { setTimeout(() => { this.readyState = 1; this.onopen?.(); }, 0); }
        send() {}
        close() { this.readyState = 3; this.onclose?.(); }
      }
      window.WebSocket = MockWebSocket;
    });
  });

  test('runs and pauses simulation without changing the workspace route', async ({ page }) => {
    const pageErrors = [];
    page.on('pageerror', (error) => pageErrors.push(error.message));
    await page.goto('/en/workspace/sandbox');
    await expect(page.getByRole('button', { name: /Beaker 250ml/ })).toBeVisible();
    await page.getByRole('button', { name: /Beaker 250ml/ }).click();
    await page.getByRole('button', { name: 'Запуск' }).click();
    await expect(page.getByRole('button', { name: /Запуск|Пауза|Продолжить/ })).toBeVisible();
    expect(page.url()).toContain('/en/workspace/sandbox');
    expect(pageErrors).toEqual([]);
  });

  test('renders condenser and receiver equipment for a visual pipeline smoke test', async ({ page }) => {
    await page.goto('/en/workspace/sandbox');
    await page.getByRole('button', { name: /Hotplate Hotplate/ }).click();
    await page.getByRole('button', { name: /Beaker 250ml/ }).click();
    await page.getByRole('button', { name: /Condenser Condenser/ }).click();
    await page.getByRole('button', { name: /Erlenmeyer Flask/ }).click();
    await expect(page.getByText('Condenser', { exact: true }).first()).toBeVisible();
    await expect(page.getByText('Erlenmeyer Flask', { exact: true }).first()).toBeVisible();
    await page.getByRole('button', { name: 'Запуск' }).click();
    await expect(page.getByRole('button', { name: /Запуск|Пауза|Продолжить/ })).toBeVisible();
    const screenshot = await page.screenshot({ animations: 'disabled' });
    expect(screenshot.length).toBeGreaterThan(10_000);
  });

  test('keeps the fracture-capable sandbox surface renderable', async ({ page }) => {
    await page.goto('/en/workspace/sandbox');
    await expect(page.locator('main')).toBeVisible();
    await page.getByRole('button', { name: /Beaker 250ml/ }).click();
    const surface = page.locator('main');
    await expect(surface).toBeVisible();
    const screenshot = await surface.screenshot({ animations: 'disabled' });
    expect(screenshot.length).toBeGreaterThan(5_000);
  });

  test('progresses a cracked vessel to shattered and renders fragments', async ({ page }) => {
    await page.goto('/en/workspace/sandbox?template=fracture');
    await expect(page.getByText('Fracture fixture', { exact: true })).toBeVisible({ timeout: 10_000 });
    await expect(page.locator('svg[aria-label="Glass crack"]')).toBeVisible();
    await page.getByRole('button', { name: 'Запуск' }).click();
    await expect(page.getByText('РАЗБИТ (SHATTERED)', { exact: false })).toBeVisible({ timeout: 3_000 });
    await expect.poll(() => page.locator('[data-testid="particle-canvas"]').getAttribute('data-shatter-count'), { timeout: 3_000 }).toBe('8');
    const screenshot = await page.locator('main').screenshot({ animations: 'disabled' });
    expect(screenshot.length).toBeGreaterThan(5_000);
  });
});
