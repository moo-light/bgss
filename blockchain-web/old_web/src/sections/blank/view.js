'use client';

import Box from '@mui/material/Box';
import { alpha } from '@mui/material/styles';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

import { useSettingsContext } from 'src/components/settings';
import ProductDetailsView from '../product/userproductdetail/userproduct-details-view';
import { useGetProduct } from 'src/api/product';

// ----------------------------------------------------------------------

export default function BlankView(prop) {
  const settings = useSettingsContext();
  const { product, productLoading, productError } = useGetProduct(prop.id);

  return (
    <Container maxWidth={settings.themeStretch ? false : 'xl'}>
      <Typography variant="h4"> Store User</Typography>
      <ProductDetailsView id={prop.id} />
      {/* <Box
        sx={{
          mt: 5,
          width: 1,
          height: 320,
          borderRadius: 2,
          bgcolor: (theme) => alpha(theme.palette.grey[500], 0.04),
          border: (theme) => `dashed 1px ${theme.palette.divider}`,
        }}
      /> */}
    </Container>
  );
}
