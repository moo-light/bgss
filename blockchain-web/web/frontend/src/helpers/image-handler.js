import toast from "react-hot-toast";
import { BASE_PATH } from "../constants/constants";

export function createPhotoURL(data, defaultifnull = null) {
  if (data == null) return defaultifnull;
  if (data != null && typeof data === "string") return data;
  const { id, name, type, imageData } = data;

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
export function getServerImgUrl(data, defaultifnull = null) {
  if (data == null) return defaultifnull; // return null if null
  if (typeof data !== "string") {
    toast.error("please import string url");
  }
  // return same if already format
  if (data != null && data?.includes(BASE_PATH)) return data;
  return `${BASE_PATH}\\${data}?${new Date().getTime()}`;
}

export function getDatabaseImgUrl(data, defaultifnull = null) {
  if (data == null) return defaultifnull; // return null if null
  if (typeof data !== "string") {
    toast.error("please import string url");
  }
  // return same if already format
  if (data != null && data?.includes(BASE_PATH))
    return data.replace(BASE_PATH + "\\", "").split("?")[0];
  return data;
}
