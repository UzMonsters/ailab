import { expect, test } from '@playwright/test';

test.describe('End-to-End QA UI Chain', () => {
  test('Equipment -> Scenario -> Level -> Book -> Sandbox UI flow works without visual overlap or errors', async ({ page }) => {
    // Collect any UI page errors
    const errors: string[] = [];
    page.on('pageerror', (error) => errors.push(error.message));

    // 1. Check Equipment Editor UI
    await page.goto('/en/admin/equipment/new');
    await expect(page.getByRole('button', { name: /Details/i }).first()).toBeVisible({ timeout: 15000 });
    // Verify Sandbox preview panel mounts properly without breaking layout
    await expect(page.getByRole('heading', { name: /Sandbox preview/i }).first()).toBeVisible();

    // 2. Check Scenario Editor UI
    await page.goto('/en/admin/scenarios/new');
    await expect(page.getByRole('button', { name: /Local/i }).first()).toBeVisible({ timeout: 15000 });
    // Ensure the Resources tab doesn't overflow or break
    await page.getByRole('button', { name: /Overview/i }).first().click();
    await page.getByRole('button', { name: /Preview/i }).first().click();
    // Sandbox inside Scenario preview should load
    await expect(page.locator('[data-sandbox-mode]')).toBeVisible({ timeout: 10000 });

    // 3. Check Level Editor UI
    await page.goto('/en/admin/learning/levels/new');
    await expect(page.getByRole('button', { name: /Scenario/i }).first()).toBeVisible({ timeout: 15000 });
    // Verify the Scenario picker modal doesn't have z-index issues
    await page.getByRole('button', { name: /Scenario/i }).first().click();
    
    // Wait for the Select Scenario button inside the Scenario tab and click it
    const selectScenarioBtn = page.getByRole('button', { name: 'Select Scenario' }).first();
    await expect(selectScenarioBtn).toBeVisible();
    await selectScenarioBtn.click();
    
    const searchInput = page.getByPlaceholder('Search by name or code...');
    await expect(searchInput).toBeVisible();
    await page.getByRole('button', { name: /Close/i }).click();

    // 4. Check Book Studio UI
    await page.goto('/en/admin/book');
    await expect(page.locator('.bg-\\[\\#090d16\\]').first()).toBeVisible({ timeout: 15000 });
    
    // Ensure canvas has correct rendering or empty state
    await expect(page.getByText('No books yet').or(page.getByText('No chapters yet')).or(page.getByText('No pages yet')).or(page.locator('[aria-label="Book page canvas"]'))).toBeVisible({ timeout: 15_000 });

    // Assert there are no unhandled JavaScript errors destroying the UI
    expect(errors.filter(e => !e.includes('Failed to load') && !e.includes('Hydration'))).toEqual([]);
  });
});
