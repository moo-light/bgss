/* eslint-disable import/no-cycle */

'use client';

import PropTypes from 'prop-types';
import { useMemo, useEffect, useReducer, useCallback } from 'react';

import axios, { endpoints } from 'src/utils/axios';
import { createPhotoURL } from 'src/utils/byte-image-handler';

import { AuthContext } from './auth-context';
import { setSession, isValidToken } from './utils';

// ----------------------------------------------------------------------
/**
 * NOTE:
 * We only build demo at basic level.
 * Customer will need to do some extra handling yourself if you want to extend the logic and other features...
 */
// ----------------------------------------------------------------------

const initialState = {
  user: null,
  loading: true,
};

const reducer = (state, action) => {
  if (action.type === INITIAL) {
    return {
      loading: false,
      user: action.payload.user,
    };
  }
  if (action.type === LOGIN) {
    return {
      ...state,
      user: action.payload.user,
    };
  }
  if (action.type === REGISTER) {
    return {
      ...state,
      user: action.payload.user,
    };
  }
  if (action.type === LOGOUT) {
    return {
      ...state,
      user: null,
    };
  }
  return state;
};

// ----------------------------------------------------------------------
const LOGIN = 'LOGIN';
const REGISTER = 'REGISTER';
const LOGOUT = 'LOGOUT';
const INITIAL = 'INITIAL';
export const STORAGE_KEY = 'accessToken';
export const STORAGE_TYPE_KEY = 'tokenType';
export const STORAGE_USERID = 'user-id';

export function AuthProvider({ children }) {
  const [state, dispatch] = useReducer(reducer, initialState);

  // Get User Data
  const getUserData = useCallback(async (id) => {
    const { data } = await axios.get(endpoints.auth.me(id));
    const { userInfo, ...user } = data.data;
    if (userInfo.avatarData) {
      userInfo.avatarData = createPhotoURL(userInfo.avatarData);
    }
    return {
      ...user,
      displayName: `${userInfo.firstName} ${userInfo.lastName}`,
      ...userInfo,
    };
  }, []);

  // ----------------------------------------------------------------------
  const initialize = useCallback(async () => {
    try {
      const accessToken = sessionStorage.getItem(STORAGE_KEY);
      const tokenType = sessionStorage.getItem(STORAGE_TYPE_KEY);
      const id = sessionStorage.getItem(STORAGE_USERID);
      if (accessToken && isValidToken(accessToken)) {
        setSession(accessToken, tokenType);
        const user = await getUserData(id);

        dispatch({
          type: 'INITIAL',
          payload: {
            user: {
              ...user,
              accessToken,
            },
          },
        });
      } else {
        dispatch({
          type: 'INITIAL',
          payload: {
            user: null,
          },
        });
      }
    } catch (error) {
      console.error(error);
      dispatch({
        type: 'INITIAL',
        payload: {
          user: null,
        },
      });
    }
  }, [getUserData]);

  useEffect(() => {
    initialize();
  }, [initialize]);

  // LOGIN
  const login = useCallback(
    async (username, password) => {
      const requestData = {
        username,
        password,
      };

      const response = await axios.post(endpoints.auth.login, requestData);

      const { accessToken, tokenType, id } = response.data;
      setSession(accessToken, tokenType);
      sessionStorage.setItem(STORAGE_USERID, id);
      const user = await getUserData(id);
      dispatch({
        type: 'LOGIN',
        payload: {
          user: {
            ...user,
            accessToken,
          },
        },
      });
    },
    [getUserData]
  );

  // REGISTER
  const register = useCallback(
    async (username, email, password, phoneNumber, firstName, lastName) => {
      const data = {
        username,
        email,
        phoneNumber,
        password,
        firstName,
        lastName,
      };
      console.log(data);
      const response = await axios.post(endpoints.auth.register, data);

      const { status } = response;
      if (status === 200) {
        await login(username, password);
      }

      // dispatch({
      //   type: 'REGISTER',
      //   payload: {
      //     user: {
      //       ...user,
      //       accessToken,
      //     },
      //   },
      // });
    },
    [login]
  );

  // LOGOUT
  const logout = useCallback(async () => {
    setSession(null, null);
    sessionStorage.removeItem(STORAGE_USERID);
    dispatch({
      type: 'LOGOUT',
    });
  }, []);

  // ----------------------------------------------------------------------

  const checkAuthenticated = state.user ? 'authenticated' : 'unauthenticated';

  const status = state.loading ? 'loading' : checkAuthenticated;

  const memoizedValue = useMemo(
    () => ({
      user: state.user,
      method: 'jwt',
      loading: status === 'loading',
      authenticated: status === 'authenticated',
      unauthenticated: status === 'unauthenticated',
      //
      login,
      register,
      logout,
    }),
    [login, logout, register, state.user, status]
  );

  return <AuthContext.Provider value={memoizedValue}>{children}</AuthContext.Provider>;
}

AuthProvider.propTypes = {
  children: PropTypes.node,
};
