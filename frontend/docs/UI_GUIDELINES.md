# Card Management System – UI Guidelines (Fintech)

Enterprise-grade, fintech-oriented design language for forms, dialogs, and pages.

## Typography

- **Page title:** 1.25rem (20px), font-weight 600 (semibold). Use for the main heading of each screen (e.g. "Users", "New Card Request").
- **Section title:** 0.95rem (15px), font-weight 500 (medium). Use for form sections (e.g. "Account", "Card details").
- **Label:** 0.875rem (14px), font-weight 400. Use for form field labels.
- **Helper / optional:** 0.8125rem (13px), muted color (`--text-color-secondary`). Use for helper text and "(optional)".
- **Font family:** Prefer a neutral sans (e.g. Inter, system-ui, or theme default). Avoid decorative fonts.

## Spacing

- **Section padding:** 24px. Use for `FormSection` and dialog body padding.
- **Between fields:** 16px. Use between consecutive form fields.
- **Dialog padding:** 24px for header/footer and body.
- **Tokens:** 8px (tight), 16px (default gap), 24px (section / card padding).

## Trust and clarity

- Use subtle borders and elevation (card shadow, dialog overlay) so content is clearly separated.
- Success/error/warning feedback: clear icons and short messages (toast or inline).
- Sensitive actions (delete, reject): two-step confirmation with explicit "Cancel" and destructive "Delete" / "Reject" buttons.

## Color and state

- **Primary:** Reserve for main actions (Submit, Save, Generate, Add). One primary button per view/dialog.
- **Semantic:** Use success (green), warning (amber), error (red) for status and validation.
- **Required vs optional:** Required fields use an asterisk; optional can show "(optional)" in helper text.
- **Disabled:** Use theme disabled color; avoid primary for disabled actions.

## Forms and dialogs

- **FormSection:** Group related fields under a section title and optional description.
- **FormField:** Every input has label, optional helper, and optional inline validation message. Required asterisk when applicable.
- **AppDialog:** Min width 32rem for create/edit; clear header (title + optional subtitle); sticky footer with secondary (Cancel) + primary (Save) actions; loading state on primary during save.
- **ConfirmActionDialog:** Standard confirm for delete/reject with clear message and explicit Cancel + destructive action.

## Page structure

- Page title and optional one-line description above the main content.
- Toolbar with primary action (e.g. "Add user", "Add Branch") and optional filters.
- Tables: consistent column headers and row density; icon-only actions with tooltips where needed.
- Empty state: short message and optional link to create (e.g. "No requests. Create one?").

## Consistency

- Use shared components (`FormSection`, `FormField`, `AppDialog`, `ConfirmActionDialog`) for all add/create flows so the experience is consistent across housekeeping, security, and card production.
