import PropTypes from 'prop-types';
import { useCallback } from 'react';

import Radio from '@mui/material/Radio';
import Stack from '@mui/material/Stack';
import Badge from '@mui/material/Badge';
import Drawer from '@mui/material/Drawer';
import Rating from '@mui/material/Rating';
import Button from '@mui/material/Button';
import Slider from '@mui/material/Slider';
import Divider from '@mui/material/Divider';
import Tooltip from '@mui/material/Tooltip';
import { alpha } from '@mui/material/styles';
import Checkbox from '@mui/material/Checkbox';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import FormControlLabel from '@mui/material/FormControlLabel';
import InputBase, { inputBaseClasses } from '@mui/material/InputBase';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';

import Iconify from './iconify';
import Scrollbar from '../dialog/scrollbar/scrollbar';

// ----------------------------------------------------------------------

function ProductFilters({
  open,
  onOpen,
  onClose,
  filters,
  onFilters,
  canReset,
  onResetFilters,
  categoryOptions,
}) {
  const marksLabel = [...Array(21)].map((_, index) => {
    const value = index * 10;

    const firstValue = index === 0 ? `$${value}` : `${value}`;

    return {
      value,
      label: index % 4 ? '' : firstValue,
    };
  });

  const handleFilterCategory = useCallback(
    (newValue) => {
      onFilters('category', newValue);
    },
    [onFilters]
  );

  const handleFilterPriceRange = useCallback(
    (event, newValue) => {
      onFilters('priceRange', newValue);
    },
    [onFilters]
  );

  const handleFilterOutOfStock = useCallback(
    (event) => {
      onFilters('outOfStock', event.target.checked);
    },
    [onFilters]
  );

  const handleFilterInsufficientWeight = useCallback(
    (event) => {
      onFilters('insufficientWeight', event.target.checked);
    },
    [onFilters]
  );

  const renderHead = (
    <Stack
      direction="row"
      alignItems="center"
      justifyContent="space-between"
      sx={{ py: 2, pr: 1, pl: 2.5 }}
    >
      <Typography variant="h6" sx={{ flexGrow: 1 }}>
        Filters
      </Typography>

      <Tooltip title="Reset">
        <IconButton onClick={onResetFilters}>
          <Badge color="error" variant="dot" invisible={!canReset}>
            <Iconify icon="solar:restart-bold" />
          </Badge>
        </IconButton>
      </Tooltip>

      <IconButton onClick={onClose}>
        <Iconify icon="mingcute:close-line" />
      </IconButton>
    </Stack>
  );

  const renderCategory = (
    <Stack>
      <Typography variant="subtitle2" sx={{ mb: 1 }}>
        Category
      </Typography>
      {categoryOptions.map((option) => (
        <FormControlLabel
          key={option}
          control={
            <Radio
              checked={option === filters.category}
              onClick={() => handleFilterCategory(option)}
            />
          }
          label={option}
          sx={{
            ...(option === 'all' && {
              textTransform: 'capitalize',
            }),
          }}
        />
      ))}
    </Stack>
  );

  const renderPrice = (
    <Stack>
      <Typography variant="subtitle2" sx={{ flexGrow: 1 }}>
        Weight
      </Typography>

      <Stack direction="row" spacing={5} sx={{ my: 2 }}>
        <InputRange type="min" value={filters.priceRange} onFilters={onFilters} />
        <InputRange type="max" value={filters.priceRange} onFilters={onFilters} />
      </Stack>

      <Slider
        value={filters.priceRange}
        onChange={handleFilterPriceRange}
        step={10}
        min={0}
        max={200}
        marks={marksLabel}
        getAriaValueText={(value) => `$${value}`}
        valueLabelFormat={(value) => `$${value}`}
        sx={{
          alignSelf: 'center',
          width: `calc(100% - 24px)`,
        }}
      />
    </Stack>
  );

  const renderStockFilters = (
    <Stack>
      <Typography variant="subtitle2" sx={{ mb: 1 }}>
        Stock Status
      </Typography>
      <FormControlLabel
        control={
          <Checkbox
            checked={filters.outOfStock}
            onChange={handleFilterOutOfStock}
          />
        }
        label="Out of Stock"
      />
      <FormControlLabel
        control={
          <Checkbox
            checked={filters.insufficientWeight}
            onChange={handleFilterInsufficientWeight}
          />
        }
        label="Insufficient Weight in Inventory"
      />
    </Stack>
  );

  return (
    <>
      <Button
        disableRipple
        color="inherit"
        endIcon={
          <Badge color="error" variant="dot" invisible={!canReset}>
            <Iconify icon="ic:round-filter-list" />
          </Badge>
        }
        onClick={onOpen}
      >
        Filters
      </Button>

      <Dialog
        open={open}
        onClose={onClose}
        fullWidth
        maxWidth="md"
      >
        <DialogContent>
          {renderHead}
          <Divider />
          <Scrollbar sx={{ px: 2.5, py: 3 }}>
            <Stack spacing={3}>
              {renderCategory}
              {renderPrice}
              {renderStockFilters}
            </Stack>
          </Scrollbar>
        </DialogContent>
      </Dialog>
    </>
  );
}

ProductFilters.propTypes = {
  open: PropTypes.bool,
  onOpen: PropTypes.func,
  onClose: PropTypes.func,
  canReset: PropTypes.bool,
  filters: PropTypes.object,
  onFilters: PropTypes.func,
  categoryOptions: PropTypes.array,
  onResetFilters: PropTypes.func,
};

export default ProductFilters;

function InputRange({ type, value, onFilters }) {
  const min = value[0];
  const max = value[1];

  const handleBlurInputRange = useCallback(() => {
    if (min < 0) {
      onFilters('priceRange', [0, max]);
    }
    if (min > 200) {
      onFilters('priceRange', [200, max]);
    }
    if (max < 0) {
      onFilters('priceRange', [min, 0]);
    }
    if (max > 200) {
      onFilters('priceRange', [min, 200]);
    }
  }, [max, min, onFilters]);

  return (
    <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ width: 1 }}>
      <Typography
        variant="caption"
        sx={{
          flexShrink: 0,
          color: 'text.disabled',
          textTransform: 'capitalize',
          fontWeight: 'fontWeightSemiBold',
        }}
      >
        {`${type} ($)`}
      </Typography>

      <InputBase
        fullWidth
        value={type === 'min' ? min : max}
        onChange={(event) =>
          type === 'min'
            ? onFilters('priceRange', [Number(event.target.value), max])
            : onFilters('priceRange', [min, Number(event.target.value)])
        }
        onBlur={handleBlurInputRange}
        inputProps={{
          step: 10,
          min: 0,
          max: 200,
          type: 'number',
          'aria-labelledby': 'input-slider',
        }}
        sx={{
          maxWidth: 48,
          borderRadius: 0.75,
          bgcolor: (theme) => alpha(theme.palette.grey[500], 0.08),
          [`& .${inputBaseClasses.input}`]: {
            pr: 1,
            py: 0.75,
            textAlign: 'right',
            typography: 'body2',
          },
        }}
      />
    </Stack>
  );
}

InputRange.propTypes = {
  onFilters: PropTypes.func,
  type: PropTypes.oneOf(['min', 'max']),
  value: PropTypes.arrayOf(PropTypes.number),
};
