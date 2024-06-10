import fs from 'fs';

export function createPhotoURL(data, defaultifnull = '') {
  if (!data) return defaultifnull; // Kiểm tra xem data có tồn tại không
  const { id, name, type, imageData } = data; // Destructure thuộc tính từ data

  // Kiểm tra imageData có tồn tại không
  if (!imageData) return defaultifnull;

  // Decode the imageData from base64 encoding to binary data
  const binary_data = atob(imageData);
  const array = [];
  for (let i = 0; i < binary_data.length; i += 1) {
    array.push(binary_data.charCodeAt(i));
  }
  const array_buffer = new Uint8Array(array);

  // Create an image blob object with the binary data and the type
  const blob = new Blob([array_buffer], { type });
  const url = URL.createObjectURL(blob);
  return url;
}
export function getPhotoURL(data) {
  // Convert binary image data to Base64
  const base64Data = data.imageData;

  // Construct data URL
  const photoURL = `data:${data.type};base64,${base64Data}`;

  return photoURL;
}
export function createImageFile(data, callback) {
  // Convert the array of bytes to a Buffer
  const buffer = Buffer.from(data.imageData);

  const fileName = `${data.name}.${data.type.split('/')[1]}`;
  // Write the buffer to a file
  fs.writeFile(fileName, buffer, (err) => {
    if (err) {
      console.error('Error creating file:', err);
      callback(err, null);
    } else {
      console.log('File created successfully.');
      const photoURL = `path/to/your/image/directory/${fileName}`; // Modify this to your actual directory path
      callback(null, photoURL);
    }
  });
}
