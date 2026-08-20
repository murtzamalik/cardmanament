/** Matches backend PasswordRules: min 8, upper, lower, special. */
export const PASSWORD_MIN_LENGTH = 8;

export const PASSWORD_RULE_HINT =
  'At least 8 characters, with 1 uppercase, 1 lowercase, and 1 special character';

export function validatePasswordStrength(password: string | undefined | null): string | null {
  if (password == null || password.trim() === '') {
    return 'Password is required';
  }
  if (password.length < PASSWORD_MIN_LENGTH) {
    return `Password must be at least ${PASSWORD_MIN_LENGTH} characters`;
  }
  if (!/[A-Z]/.test(password)) {
    return 'Password must contain at least one uppercase letter';
  }
  if (!/[a-z]/.test(password)) {
    return 'Password must contain at least one lowercase letter';
  }
  if (!/[^A-Za-z0-9\s]/.test(password)) {
    return 'Password must contain at least one special character';
  }
  return null;
}
