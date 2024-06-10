import { _mock } from 'src/_mock';

// TO GET THE USER FROM THE AUTHCONTEXT, YOU CAN USE

// CHANGE:
// import { useMockedUser } from 'src/hooks/use-mocked-user';
// const { user } = useMockedUser();

// TO:
// import { useAuthContext } from 'src/auth/hooks';
// const { user } = useAuthContext();

// ----------------------------------------------------------------------

export function useMockedUser() {
  const user = {
    id: '8864c717-587d-472a-929a-8e5f298024da-0',
    displayName: 'Nguyen Thanh Binh',
    email: 'user5@gmail.com',
    password: '123456',
    photoURL: _mock.image.avatar(24),
    phoneNumber: '+40 777666555',
    country: 'VietNam',
    address: '90210 Broadway Blvd',
    state: 'California',
    city: 'San Francisco',
    zipCode: '94116',
    about: 'Wellcome to my',
    role: 'admin',
    isPublic: true,
  };

  return { user };
}
