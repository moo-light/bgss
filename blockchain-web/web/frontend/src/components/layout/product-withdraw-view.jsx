import React, { useState, useEffect, useCallback } from 'react';
import PropTypes from 'prop-types';
import orderBy from 'lodash/orderBy';
import isEqual from 'lodash/isEqual';
import { useSelector } from 'react-redux';
import Stack from '@mui/material/Stack';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';

import { useBoolean } from './withdraw-product/hook/use-boolean';
import { useDebounce } from './withdraw-product/hook/use-debounce';
import EmptyContent from './withdraw-product/empty-content';
import { useSettingsContext } from './withdraw-product/hook/use-setting-content';
import ProductList from './withdraw-product/product-list';
import ProductSearch from '../layout/ProductSearch';
import { useSearchProducts } from '../../redux/api/product-search-api';
import ProductFiltersResult from './withdraw-product/product-filter-result';
import ProductSort from './withdraw-product/product-sort';
import ProductFilters from './withdraw-product/product-filter';


const defaultFilters = {
  gender: [],
  colors: [],
  rating: '',
  category: 'all',
  priceRange: [0, 200],
  insufficientWeight: false,
  outOfStock: false,
};

const convertToTroyOunce = (weight, unit) => {
  const conversionRates = {
    MACE: 0.12147,
    TAEL: 1.21528,
    KILOGRAM: 32.1507,
    TROY_OZ: 1,
    GRAM: 0.0321507,
  };

  return weight * conversionRates[unit];
};

function ProductShopView({ open }) {
  const { user } = useSelector((state) => state.auth);
  const settings = useSettingsContext();
  const openFilters = useBoolean();
  const [sortBy, setSortBy] = useState('weightAsc');
  const [searchQuery, setSearchQuery] = useState(' ');
  const debouncedQuery = useDebounce(searchQuery);
  const [filters, setFilters] = useState(defaultFilters);

  const { searchResults, searchLoading, searchError, searchEmpty } = useSearchProducts(debouncedQuery);

  const handleFilters = useCallback((name, value) => {
    setFilters((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  }, []);

  const handleResetFilters = useCallback(() => {
    setFilters(defaultFilters);
  }, []);

  const dataFiltered = applyFilter({
    inputData: searchResults,
    filters,
    sortBy,
    userTotalWeightTroyOz: user?.userInfo?.inventory?.totalWeightOz || 0,
  });

  const canReset = !isEqual(defaultFilters, filters);
  const notFound = !dataFiltered.length && canReset;

  const handleSortBy = useCallback((newValue) => {
    setSortBy(newValue);
  }, []);

  const handleSearch = useCallback((inputValue) => {
    setSearchQuery(inputValue);
  }, []);

  useEffect(() => {
    if (open) {
      handleSearch(searchQuery);
    }
  }, [open, searchQuery, handleSearch]);



  const renderFilters = (
    <Stack
      spacing={3}
      justifyContent="space-between"
      alignItems={{ xs: 'flex-end', sm: 'center' }}
      direction={{ xs: 'column', sm: 'row' }}
    >
      <ProductSearch
        query={debouncedQuery}
        results={searchResults}
        onSearch={handleSearch}
        loading={searchLoading}
      />

    <Stack direction="row" spacing={1} flexShrink={0}>
        <ProductFilters
          open={openFilters.value}
          onOpen={openFilters.onTrue}
          onClose={openFilters.onFalse}
          //
          filters={filters}
          onFilters={handleFilters}
          //
          canReset={canReset}
          onResetFilters={handleResetFilters}
          //
          // colorOptions={PRODUCT_COLOR_OPTIONS}
          // ratingOptions={PRODUCT_RATING_OPTIONS}
          // genderOptions={PRODUCT_GENDER_OPTIONS}
          categoryOptions={['all', ...PRODUCT_CATEGORY_OPTIONS]}
        />

        <ProductSort sort={sortBy} onSort={handleSortBy} sortOptions={PRODUCT_SORT_OPTIONS} />
      </Stack>
    </Stack>

    
  );

  const renderNotFound = <EmptyContent filled title="No Data" sx={{ py: 10 }} />;

  const renderResults = (
    <ProductFiltersResult
      filters={filters}
      onFilters={handleFilters}
      //
      canReset={canReset}
      onResetFilters={handleResetFilters}
      //
      results={dataFiltered.length}
    />
  );

  return (
    <Container
      maxWidth={settings.themeStretch ? false : 'lg'}
      sx={{
        mb: 15,
      }}
    >
      <Typography
        variant="h4"
        sx={{
          my: { xs: 3, md: 5 },
        }}
      >
        Products
      </Typography>

      <Stack
        spacing={2.5}
        sx={{
          mb: { xs: 3, md: 5 },
        }}
      >
        {renderFilters}

        {canReset && renderResults}
      </Stack>

      {(notFound || searchEmpty) && renderNotFound}

      <ProductList products={dataFiltered} loading={searchLoading} />
    </Container>
  );
}

ProductShopView.propTypes = {
  user: PropTypes.object,
  open: PropTypes.bool,
};

export default ProductShopView;

export const PRODUCT_SORT_OPTIONS = [
  // { value: 'featured', label: 'Featured' },
  // { value: 'newest', label: 'Newest' },
  // { value: 'priceDesc', label: 'Price: High - Low' },
  // { value: 'priceAsc', label: 'Price: Low - High' },
  { value: 'weightAsc', label: 'Weight: Low - High' },
  { value: 'weightDesc', label: 'Weight: High - Low' } 
];

function applyFilter({ inputData, filters, sortBy, userTotalWeightTroyOz }) {
  const { gender, category, colors, priceRange, rating, outOfStock, insufficientWeight } = filters;

  const min = priceRange[0];
  const max = priceRange[1];



  // SORT BY
  if (sortBy === 'featured') {
    inputData = orderBy(inputData, ['totalSold'], ['desc']);
  }

  if (sortBy === 'newest') {
    inputData = orderBy(inputData, ['createdAt'], ['desc']);
  }

  if (sortBy === 'priceDesc') {
    inputData = orderBy(inputData, ['price'], ['desc']);
  }

  if (sortBy === 'priceAsc') {
    inputData = orderBy(inputData, ['price'], ['asc']);
  }

  if (sortBy === 'weightAsc') {
    inputData = orderBy(inputData, ['weight'], ['asc']);
  }

  if (sortBy === 'weightDesc') {
    inputData = orderBy(inputData, ['weight'], ['desc']);
  }

  // FILTERS
  if (gender.length) {
    inputData = inputData.filter((product) => gender.includes(product.gender));
  }

  if (category !== 'all') {
    inputData = inputData.filter((product) => product.category === category);
  }

  if (colors.length) {
    inputData = inputData.filter((product) =>
      product.colors.some((color) => colors.includes(color))
    );
  }

  

  if (min !== 0 || max !== 200) {
    inputData = inputData.filter((product) => product.weight >= min && product.weight <= max);
  }

  if (rating) {
    inputData = inputData.filter((product) => {
      const convertRating = (value) => {
        if (value === 'up4Star') return 4;
        if (value === 'up3Star') return 3;
        if (value === 'up2Star') return 2;
        return 1;
      };
      return product.totalRatings > convertRating(rating);
    });
  }

  if (outOfStock) {
    inputData = inputData.filter((product) => product.unitOfStock === 0);
  }

  if (insufficientWeight) {
    inputData = inputData.filter((product) => {
      const productWeightTroyOz = convertToTroyOunce(product.weight, product.unit);
      return userTotalWeightTroyOz < productWeightTroyOz;
    });
  }

  return inputData;
}

export const PRODUCT_CATEGORY_OPTIONS = [
  'Wedding ring',
  'Engagement ring',
  'Bracelet',
  'Necklace',
  'Earrings',
  'Ring',
];