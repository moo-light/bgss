import * as Yup from 'yup';
import { useCallback } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';

import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Stack from '@mui/material/Stack';
import Grid from '@mui/material/Unstable_Grid2';
import Typography from '@mui/material/Typography';
import LoadingButton from '@mui/lab/LoadingButton';

import { fData } from 'src/utils/format-number';
import axiosInstance, { endpoints } from 'src/utils/axios';
import { createPhotoURL } from 'src/utils/byte-image-handler';

import { HOST_API } from 'src/config-global';
import { useAuthContext } from 'src/auth/hooks';

import { useSnackbar } from 'src/components/snackbar';
import FormProvider, { RHFTextField, RHFUploadAvatar } from 'src/components/hook-form';

// ----------------------------------------------------------------------
export default function AccountGeneral() {
  const { enqueueSnackbar } = useSnackbar();

  const { user } = useAuthContext();
  const UpdateUserNameSchema = Yup.object().shape({
    // displayName: Yup.string().required('Name is required'),
    // email: Yup.string().required('Email is required').email('Email must be a valid email address'),
    firstName: Yup.string().required('First Name is required'),
    lastName: Yup.string().required('Last Name is required'),
  });
  const UpdateUserAvatarSchema = Yup.object().shape({
    photoURL: Yup.mixed().nullable().required('Avatar is required'),
    // not required
    // isPublic: Yup.boolean(),
  });
  const phoneRegExp =
    /^((\\+[1-9]{1,4}[ \\-]*)|(\\([0-9]{2,3}\\)[ \\-]*)|([0-9]{2,4})[ \\-]*)*?[0-9]{3,4}?[ \\-]*[0-9]{3,4}?$/;
  const UpdateUserPhoneSchema = Yup.object().shape({
    phoneNumber: Yup.string()
      .matches(phoneRegExp, 'Phone is Invalid')
      .required('Phone number is required'),
  });
  const defaultAvatarValues = {
    photoURL: user?.avatarData || '',
  };
  const defaultNameValues = {
    email: user?.email || '',
    username: user?.username || '',
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
  };
  const UpdateUserSchema = Yup.object().shape({
    address: Yup.string().required('Address is required'),
    // not required
    // isPublic: Yup.boolean(),
  });
  const defaultPhoneValues = {
    phoneNumber: user?.phoneNumber || '',
  };
  const methodAvatars = useForm({
    resolver: yupResolver(UpdateUserAvatarSchema),
    defaultValues: defaultAvatarValues,
  });
  const methodNames = useForm({
    resolver: yupResolver(UpdateUserNameSchema),
    defaultValues: defaultNameValues,
  });

  const methodPhones = useForm({
    resolver: yupResolver(UpdateUserPhoneSchema), // Assuming you have a separate schema for phone
    defaultValues: defaultPhoneValues, // Assuming you have separate default values for phone
  });
  const {
    setValue: setAvatarValue,
    handleSubmit: handleSubmitAvatar,
    formState: { isSubmitting: isSubmittingAvatar },
  } = methodAvatars;

  const {
    setValue: setNameValue,
    handleSubmit: handleSubmitName,
    formState: { isSubmitting: isSubmittingName },
  } = methodNames;

  const {
    setValue: setPhoneValue,
    handleSubmit: handleSubmitPhone,
    formState: { isSubmitting: isSubmittingPhone },
  } = methodPhones;
  const onAvatarSubmit = handleSubmitAvatar(async (data) => {
    try {
      console.log('Data:', data);
      const formData = new FormData();
      formData.append('imageData', data.photoURL, `user${user.id}_avatar`);

      const config = {
        url: `${HOST_API}${endpoints.user.updateAvatar(user.id)}`,
        headers: {
          'Content-Type': 'multipart/form-data', // Remove boundary from Content-Type
        },
      };
      const response = await axiosInstance.post(`${config.url}`, formData);

      if (response.status === 200) {
        enqueueSnackbar('Update Avatar success!');
        setAvatarValue('photoURL', createPhotoURL(user.avatarData));
      } else {
        throw new Error('Failed to update avatar');
      }

      console.log('Response Data:', response.data);
    } catch (error) {
      console.error('Error updating avatar:', error);
    }
  });

  const onNameSubmit = handleSubmitName(async (data) => {
    try {
      const response = await axiosInstance.post(
        endpoints.user.updateUserName(user.id),
        {
          firstName: data.firstName,
          lastName: data.lastName,
        }
        // {
        //   'Content-Type': 'application/json',
        // }
      );
      if (response.status === 200) {
        enqueueSnackbar('Update User Name success!');
      }
      console.info('DATA', data);
    } catch (error) {
      console.error(error);
    }
  });
  const onPhoneSubmit = handleSubmitPhone(async (data) => {
    try {
      const response = await axiosInstance.post(endpoints.user.updateInfo, {
        userId: user.id,
        firstName: user.firstName,
        lastName: user.lastName,
        ...data,
      });
      if (response.status === 200) enqueueSnackbar('Update Phone success!');
      console.info('DATA', data);
    } catch (error) {
      console.error(error);
    }
  });

  const handleDrop = useCallback(
    (acceptedFiles) => {
      const file = acceptedFiles[0];

      const newFile = Object.assign(file, {
        preview: URL.createObjectURL(file),
      });

      if (file) {
        setAvatarValue('photoURL', newFile, { shouldValidate: true });
      }
    },
    [setAvatarValue]
  );

  return (
    <Grid container spacing={3}>
      <Grid xs={12} md={4}>
        <Card sx={{ pt: 10, pb: 5, px: 3, textAlign: 'center' }}>
          <FormProvider
            methods={methodAvatars}
            resolver={UpdateUserAvatarSchema}
            onSubmit={onAvatarSubmit}
          >
            <RHFUploadAvatar
              name="photoURL"
              maxSize={3145728}
              onDrop={handleDrop}
              helperText={
                <Typography
                  variant="caption"
                  sx={{
                    mt: 3,
                    mx: 'auto',
                    display: 'block',
                    textAlign: 'center',
                    color: 'text.disabled',
                  }}
                >
                  Allowed *.jpeg, *.jpg, *.png, *.gif
                  <br /> max size of {fData(3145728)}
                </Typography>
              }
            />
            {/* <RHFSwitch
              name="isPublic"
              labelPlacement="start"
              label="Public Profile"
              sx={{ mt: 5 }}
            /> */}

            <LoadingButton type="submit" sx={{ mt: 3 }} variant="soft" loading={isSubmittingAvatar}>
              Save Changes
            </LoadingButton>
          </FormProvider>
        </Card>
      </Grid>
      <Grid xs={12} md={8}>
        <Grid sx={{ mb: 3 }}>
          <Card sx={{ p: 3 }}>
            <FormProvider
              methods={methodNames}
              resolver={UpdateUserNameSchema}
              onSubmit={onNameSubmit}
            >
              <Box
                rowGap={3}
                columnGap={2}
                display="grid"
                gridTemplateColumns={{
                  xs: 'repeat(1, 1fr) auto',
                  sm: 'repeat(2, 1fr) auto',
                }}
              >
                <RHFTextField name="username" label="Username" inputProps={{ readOnly: true }} />
                <RHFTextField
                  name="email"
                  label="Email Address"
                  inputProps={{
                    readOnly: true,
                  }}
                />
              </Box>
            </FormProvider>
          </Card>
        </Grid>
        <Grid sx={{ mb: 3 }}>
          <Card sx={{ p: 3 }}>
            <FormProvider
              methods={methodNames}
              resolver={UpdateUserNameSchema}
              onSubmit={onNameSubmit}
            >
              <Box
                rowGap={3}
                columnGap={2}
                display="grid"
                gridTemplateColumns={{
                  xs: 'repeat(1, 1fr) auto',
                  sm: '2fr 2fr auto',
                }}
              >
                <RHFTextField name="firstName" label="First Name" sx={{ flexGrow: 1 }} />
                <RHFTextField name="lastName" label="Last Name" sx={{ flexGrow: 1 }} />
                <Stack
                  spacing={3}
                  sx={{ width: 'auto' }}
                  alignItems={{
                    md: 'flex-end',
                    xs: 'flex-start',
                  }}
                  justifyContent="center"
                >
                  <LoadingButton type="submit" variant="contained" loading={isSubmittingName}>
                    Save Changes
                  </LoadingButton>
                </Stack>
                {/* <RHFTextField name="state" label="State/Region" />
  <RHFTextField name="city" label="City" />
<RHFTextField name="zipCode" label="Zip/Code" /> */}
              </Box>
            </FormProvider>
          </Card>
        </Grid>

        <Grid>
          <Card sx={{ p: 3 }}>
            <FormProvider
              methods={methodPhones}
              resolver={UpdateUserPhoneSchema}
              onSubmit={onPhoneSubmit}
            >
              <Box
                rowGap={3}
                columnGap={2}
                display="grid"
                justifyContent="center"
                gridTemplateColumns={{
                  xs: '4fr auto',
                }}
              >
                <RHFTextField name="phoneNumber" label="Phone Number" />
                <Stack
                  spacing={3}
                  justifyContent="center"
                  alignItems={{
                    md: 'flex-end',
                    xs: 'flex-start',
                  }}
                >
                  <LoadingButton type="submit" variant="contained" loading={isSubmittingPhone}>
                    Save Changes
                  </LoadingButton>
                </Stack>
              </Box>
            </FormProvider>
          </Card>
        </Grid>
      </Grid>
    </Grid>
  );
}
