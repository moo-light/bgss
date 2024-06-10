import PropTypes from "prop-types";

import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import Fab from "@mui/material/Fab";
import Stack from "@mui/material/Stack";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";

import { useState } from "react";
import { useSelector } from "react-redux";
import { BASE_HOST, BASE_PRODUCTIMG } from "../../../constants/constants";
import { getServerImgUrl } from "../../../helpers/image-handler";
import Iconify from "../withdraw-product/iconify";
import TransitionsDialog from "./dialog/transitions-dialog";
import Image from "./images";

// ----------------------------------------------------------------------

function ProductItem({ product }) {
  const HOST = BASE_HOST;

  const { user } = useSelector((state) => state.auth);

  // Cập nhật cách trích xuất dữ liệu từ prop `product` để phản ánh cấu trúc mới
  const {
    id,
    name,
    unitOfStock: available,
    coverUrl,
    weight,
    typeGold,
    unit,
  } = product;

  const [open, setOpen] = useState(false);

  const handleOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
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

  const userTotalWeightTroyOz = user?.userInfo?.inventory?.totalWeightOz || 0;
  const productWeightTroyOz = convertToTroyOunce(weight, unit);

  const userHasSufficientWeight =
    userTotalWeightTroyOz.toFixed(2) >= productWeightTroyOz;

  const goldType = typeGold.split(" ")[0];

  const renderImg = (
    <>
      <Box sx={{ position: "relative", p: 1 }}>
        {!!available && userHasSufficientWeight && (
          <Fab
            color="warning"
            size="medium"
            className="add-cart-btn"
            onClick={handleOpen}
            sx={{
              right: 16,
              bottom: 16,
              zIndex: 9,
              opacity: 0,
              position: "absolute",
              transition: (theme) =>
                theme.transitions.create("all", {
                  easing: theme.transitions.easing.easeInOut,
                  duration: theme.transitions.duration.shorter,
                }),
            }}
          >
            <Iconify icon="solar:cart-plus-bold" width={24} />
          </Fab>
        )}

        <Tooltip
          title={
            !available
              ? "Out of stock"
              : !userHasSufficientWeight
              ? "Insufficient weight in Inventory"
              : ""
          }
          placement="bottom-end"
        >
          <Image
            alt={name}
            src={getServerImgUrl(coverUrl, BASE_PRODUCTIMG)}
            ratio="1/1"
            sx={{
              borderRadius: 4,
              ...((!available || !userHasSufficientWeight) && {
                opacity: 0.48,
                filter: "grayscale(1)",
              }),
            }}
          />
        </Tooltip>
      </Box>
      <TransitionsDialog
        openTransition={open}
        closeTransition={handleClose}
        productId={id}
        productWeightTroyOz={productWeightTroyOz.toFixed(2)}
        unit={unit}
        weight={weight}
      />
    </>
  );

  const renderContent = (
    <Stack spacing={2.5} sx={{ p: 3, pt: 2 }}>
      <Typography variant="subtitle1" noWrap color="inherit">
        {name}
      </Typography>

      <Stack direction="row" alignItems="center" justifyContent="space-between">
        <Stack direction="row" spacing={0.5} sx={{ typography: "subtitle2" }}>
          <Box component="span">
            Weight: {weight} {unit}
          </Box>
        </Stack>
        <Typography variant="subtitle2" noWrap color="inherit">
          Available: {available}
        </Typography>
      </Stack>
    </Stack>
  );

  return (
    <Card
      sx={{
        "&:hover .add-cart-btn": {
          opacity: 1,
        },
        borderRadius: 4,
      }}
    >
      {renderImg}

      {renderContent}
    </Card>
  );
}

ProductItem.propTypes = {
  product: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    unitOfStock: PropTypes.number.isRequired,
    coverUrl: PropTypes.string.isRequired,
    weight: PropTypes.number.isRequired,
    typeGold: PropTypes.string.isRequired,
    unit: PropTypes.oneOf(["MACE", "TAEL", "KILOGRAM", "TROY_OZ", "GRAM"])
      .isRequired,
  }).isRequired,
};

export default ProductItem;
