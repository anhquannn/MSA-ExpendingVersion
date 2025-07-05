// src/utils/jwt.ts
// Simple JWT payload decoder (client-side, without verifying signature)
// Use ONLY for extracting non-sensitive info such as scope, branchId etc.
export interface JwtPayload {
  [key: string]: any;
}

export function decodeJwt(token: string): JwtPayload | null {
  if (!token) return null;
  const parts = token.split('.');
  if (parts.length < 2) return null;
  try {
    const payloadBase64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const decoded = atob(payloadBase64);
    return JSON.parse(decoded);
  } catch (err) {
    console.error('Failed to decode JWT', err);
    return null;
  }
}

export function extractBranchIdFromScope(scopeStr: string | undefined): number | undefined {
  if (!scopeStr) return undefined;
  const match = scopeStr.match(/ROLE_MANAGER_(\d+)/);
  if (match) {
    return Number(match[1]);
  }
  return undefined;
}
