/* eslint-disable import/no-cycle */
import { paths } from 'src/routes/paths';

import axios from 'src/utils/axios';

import { STORAGE_KEY, STORAGE_TYPE_KEY } from './auth-provider';

// ----------------------------------------------------------------------

function jwtDecode(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    window
      .atob(base64)
      .split('')
      .map((c) => `%${`00${c.charCodeAt(0).toString(16)}`.slice(-2)}`)
      .join('')
  );

  return JSON.parse(jsonPayload);
}

// ----------------------------------------------------------------------

export const isValidToken = (accessToken) => {
  if (!accessToken) {
    return false;
  }

  const decoded = jwtDecode(accessToken);

  const currentTime = Date.now() / 1000;

  return decoded.exp > currentTime;
};

// ----------------------------------------------------------------------

export const tokenExpired = (exp, iat) => {
  // eslint-disable-next-line prefer-const
  let expiredTimer;

  const currentTime = Date.now();

  // Test token expires after 10s
  // const timeLeft = currentTime + 10000 - currentTime; // ~10s
  const timeLeft = (exp - iat) * 1000;
  // + currentTime
  console.log(exp - iat, timeLeft - currentTime);
  clearTimeout(expiredTimer);

  expiredTimer = setTimeout(() => {
    alert('Token expired');

    sessionStorage.removeItem(STORAGE_KEY);

    window.location.href = paths.auth.jwt.login;
  }, timeLeft);
};

// ----------------------------------------------------------------------

export const setSession = (accessToken, type) => {
  if (accessToken) {
    sessionStorage.setItem(STORAGE_KEY, accessToken);
    sessionStorage.setItem(STORAGE_TYPE_KEY, type);
    axios.defaults.headers.Authorization = `${type} ${accessToken}`;
    // This function below will handle when token is expired
    const { exp, iat } = jwtDecode(accessToken); // ~3 days by minimals server
    tokenExpired(exp, iat);
  } else {
    sessionStorage.removeItem(STORAGE_KEY);

    delete axios.defaults.headers.common.Authorization;
  }
};
