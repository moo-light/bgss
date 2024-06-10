export function generateOrderQRImg(qrCode) {
  return `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=OR!${qrCode}`;
}
export function generateWithdrawQRImg(qrCode) {
  return `https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=WD!${qrCode}`;
}
