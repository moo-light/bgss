export const colorFormatWithdraw = (value) => {
  switch (value.toUpperCase()) {
    case "COMPLETED":
      return "text-success";
    case "PENDING":
      return "gold";
    case "CONFIRMED":
      return "text-success";
    case "CANCELLED":
      return "text-danger";
    default:
      return "";
  }
};
