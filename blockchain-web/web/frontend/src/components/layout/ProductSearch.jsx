import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import parse from 'autosuggest-highlight/parse';
import match from 'autosuggest-highlight/match';

import Box from '@mui/material/Box';
import Avatar from '@mui/material/Avatar';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import InputAdornment from '@mui/material/InputAdornment';
import Autocomplete, { autocompleteClasses } from '@mui/material/Autocomplete';
import Iconify from '../../helpers/components/Icontify';
import SearchNotFound from '../../helpers/components/search-not-found';
import { BASE_HOST } from '../../constants/constants';

function ProductSearch({ query, results, onSearch, loading, onProductSelect }) {
  const [inputValue, setInputValue] = useState(query);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [open, setOpen] = useState(false);
  const HOST = BASE_HOST;




  const handleSelect = (event, newValue) => {
    const productName = newValue?.name || '';
    setSelectedProduct(productName);
    setOpen(false);
    onProductSelect(productName);
  };

  const handleKeyUp = (event) => {
    if (event.key === 'Enter' && event.text === '') {
      onSearch(query);
    }
  };

  // useEffect(() => {
  //   if (inputValue === ' ') {
  //     onSearch(' ');
  //   }
  // }, [inputValue, onSearch]);

  const getOptionLabel = (option) => `${option.name} (${option.id})`;

  return (
    <>
      <Autocomplete
        open={open}
        onOpen={() => {
          setOpen(true);
          onSearch(query);
        }}
        onClose={() => setOpen(false)}
        sx={{ width: { xs: 1, sm: 260 } }}
        loading={loading}
        autoHighlight
        popupIcon={null}
        options={results}
        onInputChange={(event, newValue) => onSearch(newValue)}
        getOptionLabel={getOptionLabel}
        noOptionsText={<SearchNotFound query={query} sx={{ bgcolor: 'unset' }} />}
        isOptionEqualToValue={(option, value) => option.id === value.id}
        slotProps={{
          popper: {
            placement: 'bottom-start',
            sx: {
              minWidth: 320,
            },
          },
          paper: {
            sx: {
              [` .${autocompleteClasses.option}`]: {
                pl: 0.75,
              },
            },
          },
        }}
        renderInput={(params) => (
          <TextField
            {...params}
            placeholder="Choose Product..."
            onKeyUp={handleKeyUp}
            InputProps={{
              ...params.InputProps,
              startAdornment: (
                <InputAdornment position="start">
                  <Iconify icon="eva:search-fill" sx={{ ml: 1, color: 'text.disabled' }} />
                </InputAdornment>
              ),
              endAdornment: (
                <>
                  {loading ? <Iconify icon="svg-spinners:8-dots-rotate" sx={{ mr: -3 }} /> : null}
                  {params.InputProps.endAdornment}
                </>
              ),
            }}
          />
        )}
        renderOption={(props, product, { inputValue }) => {
          const matches = match(product.name, inputValue);
          const parts = parse(product.name, matches);

          return (
            <Box component="li" {...props} >
              <Avatar
                key={product.id}
                alt={product.name}
                src={`${HOST}/${product.coverUrl}`}
                variant="rounded"
                sx={{
                  width: 48,
                  height: 48,
                  flexShrink: 0,
                  mr: 1.5,
                  borderRadius: 1,
                }}
              />
              <div key={inputValue}>
                {parts.map((part, index) => (
                  <Typography
                    key={index}
                    component="span"
                    color={part.highlight ? 'primary' : 'textPrimary'}
                    sx={{
                      typography: 'body2',
                      fontWeight: part.highlight ? 'fontWeightSemiBold' : 'fontWeightMedium',
                    }}
                  >
                    {part.text}
                  </Typography>
                ))}
              </div>
            </Box>
          );
        }}
        // onChange={handleSelect}
      />
    </>
  );
}

ProductSearch.propTypes = {
  loading: PropTypes.bool,
  onSearch: PropTypes.func,
  query: PropTypes.string,
  results: PropTypes.array,
  onProductSelect: PropTypes.func,
};

export default ProductSearch;
