import React, { forwardRef } from 'react';
import PropTypes from 'prop-types';
import { Dialog, AppBar, Toolbar, IconButton, Typography, Button, Slide } from '@mui/material';
import Iconify from '../withdraw-product/iconify';
import ProductShopView from '../product-withdraw-view';

const Transition = forwardRef((props, ref) => <Slide direction="up" ref={ref} {...props} />);

function FullScreenDialog({ open, onClose, fetchSearchResults }) {
  return (
    <Dialog fullScreen open={open} TransitionComponent={Transition} onClose={onClose}>
      <AppBar position="relative" color="default">
        <Toolbar>
          <IconButton color="inherit" edge="start" onClick={onClose}>
            <Iconify icon="mingcute:close-line" />
          </IconButton>
          <Typography variant="h6" sx={{ flex: 1, ml: 2 }}>
            Withdraw Gold
          </Typography>
          <Button autoFocus color="inherit" variant="contained" onClick={onClose}>
            Close
          </Button>
        </Toolbar>
      </AppBar>
      <ProductShopView open={fetchSearchResults} />
    </Dialog>
  );
}

FullScreenDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};

export default FullScreenDialog;
