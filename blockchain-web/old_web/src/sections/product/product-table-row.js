import PropTypes from 'prop-types';

import Box from '@mui/material/Box';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Avatar from '@mui/material/Avatar';
import ListItemText from '@mui/material/ListItemText';
import LinearProgress from '@mui/material/LinearProgress';

import { fCurrency } from 'src/utils/format-number';
import { fTime, fDate } from 'src/utils/format-time';

import Label from 'src/components/label';
import { createPhotoURL } from 'src/utils/byte-image-handler';

// ----------------------------------------------------------------------

export function RenderCellPrice({ params }) {
  return <>{fCurrency(params.row.price)}</>;
}

RenderCellPrice.propTypes = {
  params: PropTypes.shape({
    row: PropTypes.object,
  }),
};

export function RenderCellPublish({ params }) {
  return (
    <Label variant="soft" color={(params.row.publish === 'published' && 'info') || 'default'}>
      {params.row.publish}
    </Label>
  );
}

RenderCellPublish.propTypes = {
  params: PropTypes.shape({
    row: PropTypes.object,
  }),
};

export function RenderCellCreatedAt({ params }) {
  return (
    <ListItemText
      primary={fDate(params.row.createdAt)}
      secondary={fTime(params.row.createdAt)}
      primaryTypographyProps={{ typography: 'body2', noWrap: true }}
      secondaryTypographyProps={{
        mt: 0.5,
        component: 'span',
        typography: 'caption',
      }}
    />
  );
}

RenderCellCreatedAt.propTypes = {
  params: PropTypes.shape({
    row: PropTypes.object,
  }),
};

export function RenderCellStock({ params }) {
  return (
    <Stack sx={{ typography: 'caption', color: 'text.secondary' }}>
      <LinearProgress
        value={(params.row.unitOfStock * 100) / params.row.quantity}
        variant="determinate"
        color={
          (params.row.inventoryType === 'out of stock' && 'error') ||
          (params.row.inventoryType === 'low stock' && 'warning') ||
          'success'
        }
        sx={{ mb: 1, height: 6, maxWidth: 80 }}
      />
      {!!params.row.unitOfStock && params.row.unitOfStock} {params.row.inventoryType}
    </Stack>
  );
}

RenderCellStock.propTypes = {
  params: PropTypes.shape({
    row: PropTypes.object,
  }),
};

// export function RenderCellProduct({ params }) {
//   return (
//     <Stack direction="row" alignItems="center" sx={{ py: 2, width: 1 }}>
//       <Avatar
//         alt={params.row.name}
//         src={params.row.coverUrl}
//         variant="rounded"
//         sx={{ width: 64, height: 64, mr: 2 }}
//       />

//       <ListItemText
//         disableTypography
//         primary={
//           <Link
//             noWrap
//             color="inherit"
//             variant="subtitle2"
//             onClick={params.row.onViewRow}
//             sx={{ cursor: 'pointer' }}
//           >
//             {params.row.name}
//           </Link>
//         }
//         secondary={
//           <Box component="div" sx={{ typography: 'body2', color: 'text.disabled' }}>
//             {params.row.category}
//           </Box>
//         }
//         sx={{ display: 'flex', flexDirection: 'column' }}
//       />
//     </Stack>
//   );
// }
export function RenderCellProductName({ params }) {
  return (
    <Stack direction="column" alignItems="start" sx={{ py: 2, width: 1 }}>
      <ListItemText
        disableTypography
        primary={
          <Box component="div" sx={{ typography: 'body1', color: 'text.primary' }}>
            {params.row.productName}
          </Box>
        }
      />
    </Stack>
  );
}
export function RenderCellProductImage({ params }) {
  const defaultLogoUrl = "/logo/logo_goldImage.png";

  // Gọi hàm createPhotoURL để xử lý productImage
  const processedImage = createPhotoURL(params.row.productImage);

  return (
    <Stack direction="column" alignItems="start" sx={{ py: 2, width: 1 }}>
      <Avatar
        alt={params.row.productName}
        src={processedImage || defaultLogoUrl}
        variant="rounded"
        sx={{ width: 64, height: 64, mr: 2 }}
      />
    </Stack>
  );
}



export function RenderCellDescription({ params }) {
  return (
    <Stack direction="column" alignItems="start" sx={{ py: 2, width: 1 }}>
      <ListItemText
        disableTypography
        primary={
          <Box component="div" sx={{ typography: 'body1', color: 'text.primary' }}>
            {params.row.description}
          </Box>
        }
      />
    </Stack>
  );
}

// RenderCellProduct.propTypes = {
//   params: PropTypes.shape({
//     row: PropTypes.object,
//   }),
// };
