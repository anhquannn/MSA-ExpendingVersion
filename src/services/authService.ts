import { LoginCredentials, Platform } from '../interfaces/auth.interface';

export const DEFAULT_PLATFORM = Platform.WEB;

export const prepareLoginCredentials = (credentials: LoginCredentials): LoginCredentials => ({
  ...credentials,
  platform: credentials.platform || DEFAULT_PLATFORM,
});
