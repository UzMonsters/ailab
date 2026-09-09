import { expect, test } from '@playwright/test';

test('free Sandbox mounts in NORMAL mode without page errors', async ({ page }) => {
  const errors: string[] = [];
  page.on('pageerror', (error) => errors.push(error.message));
  await page.goto('/en/workspace/sandbox');
  await expect(page.locator('.sandbox-ui')).toBeVisible({ timeout: 20_000 });
  await expect(page.locator('[data-sandbox-mode="NORMAL"]')).toBeVisible();
  await expect(page.locator('[data-testid="sandbox-sync-status"]')).toBeVisible();
  expect(errors).toEqual([]);
});

test('numeric legacy Level remains a compatibility route', async ({ page }) => {
  await page.goto('/en/workspace/sandbox?level=1');
  await expect(page.locator('.sandbox-ui')).toBeVisible({ timeout: 20_000 });
  await expect(page.locator('[data-sandbox-mode="LEARNING"]')).toBeVisible();
  await expect(page.getByText('SCENARIO PROGRESS')).toBeVisible();
  await expect(page.getByText('Sandbox introduction')).toBeVisible();
});

test('admin entry remains English or redirects to authentication', async ({ page }) => {
  await page.goto('/en/admin/scenarios');
  await expect(page).toHaveURL(/\/en\/(admin\/scenarios|auth)/);
  if (page.url().includes('/admin/scenarios')) await expect(page.getByText('Scenarios', { exact: true }).first()).toBeVisible();
  else await expect(page.locator('body')).toContainText(/sign|log|auth/i);
});
