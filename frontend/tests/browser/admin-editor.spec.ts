import { expect, test } from '@playwright/test';

test.describe('Book Studio', () => {
  test('Book Studio loads with editor shell', async ({ page }) => {
    const errors: string[] = [];
    page.on('pageerror', (error) => errors.push(error.message));
    await page.goto('/en/admin/book');
    await expect(page.locator('.bg-\\[\\#090d16\\]').first()).toBeVisible({ timeout: 20_000 });
    expect(errors.filter(e => !e.includes('Failed to load')).length).toBe(0);
  });

  test('Book Studio shows onboarding when no book', async ({ page }) => {
    await page.goto('/en/admin/book');
    await expect(page.getByText('No books yet').or(page.getByText('No chapters yet')).or(page.getByText('No pages yet')).or(page.locator('[aria-label="Book page canvas"]'))).toBeVisible({ timeout: 20_000 });
  });

  test('Book Studio has no native confirm or prompt', async ({ page }) => {
    let nativeConfirmCalled = false;
    let nativePromptCalled = false;
    page.on('dialog', (dialog) => {
      if (dialog.type() === 'confirm') nativeConfirmCalled = true;
      if (dialog.type() === 'prompt') nativePromptCalled = true;
      dialog.dismiss();
    });
    await page.goto('/en/admin/book');
    await page.waitForTimeout(3000);
    expect(nativeConfirmCalled).toBe(false);
    expect(nativePromptCalled).toBe(false);
  });
});

test.describe('Scenario Editor', () => {
  test('Scenario list loads', async ({ page }) => {
    await page.goto('/en/admin/scenarios');
    await expect(page.getByText('Scenarios', { exact: true }).first()).toBeVisible({ timeout: 20_000 });
  });

  test('New Scenario editor loads with tabs', async ({ page }) => {
    await page.goto('/en/admin/scenarios/new');
    await expect(page.getByRole('button', { name: /Overview|Local|Step|Preview|Valid/i }).first()).toBeVisible({ timeout: 20_000 });
  });

  test('Scenario preview tab exists', async ({ page }) => {
    await page.goto('/en/admin/scenarios/new');
    await page.getByRole('button', { name: /Preview/i }).first().click();
    await expect(page.getByText('Interactive admin preview')).toBeVisible({ timeout: 10_000 });
  });

  test('No duplicate Sandbox in preview tab', async ({ page }) => {
    await page.goto('/en/admin/scenarios/new');
    await page.getByRole('button', { name: /Preview/i }).first().click();
    await page.waitForTimeout(2000);
    const sandboxCount = await page.locator('[data-sandbox-mode]').count();
    expect(sandboxCount).toBeLessThanOrEqual(1);
  });
});

test.describe('Learning Levels', () => {
  test('Level list loads', async ({ page }) => {
    await page.goto('/en/admin/learning/levels');
    await expect(page.getByText('Learning Levels').first()).toBeVisible({ timeout: 20_000 });
  });

  test('New Level editor loads with correct tabs', async ({ page }) => {
    await page.goto('/en/admin/learning/levels/new');
    await expect(page.getByRole('button', { name: /Details/i }).first()).toBeVisible({ timeout: 20_000 });
    await expect(page.getByRole('button', { name: /Localization/i }).first()).toBeVisible();
    await expect(page.getByRole('button', { name: /Scenario/i }).first()).toBeVisible();
    await expect(page.getByRole('button', { name: /Preview/i }).first()).toBeVisible();
    await expect(page.getByRole('button', { name: /Validation/i }).first()).toBeVisible();
  });

  test('Level editor does not contain legacy fields', async ({ page }) => {
    await page.goto('/en/admin/learning/levels/new');
    await expect(page.getByText('Checkpoint fact')).not.toBeVisible({ timeout: 10_000 });
    await expect(page.getByText('Guide kind')).not.toBeVisible();
    await expect(page.getByText('Step type')).not.toBeVisible();
  });

  test('Level preview shows LevelIntro', async ({ page }) => {
    await page.goto('/en/admin/learning/levels/new');
    await page.getByRole('button', { name: /Preview/i }).first().click();
    await expect(page.getByText('Show Level Intro Modal')).toBeVisible({ timeout: 10_000 });
  });
});
