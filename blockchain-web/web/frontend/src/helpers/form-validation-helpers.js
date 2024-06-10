import { MAXFILE_SIZE, SUPPORTED_IMAGE_FORMATS } from "../constants/constants";

export function addErrors(document, errors) {
  if (!errors) return;
  var entries = Object.keys(errors)
    .filter((key) => errors[key] != null)
    .filter((key) => errors[key] == "");
  //   console.log(
  //     entries.map((entry) => {
  //       return { entry, value: errors[entry] };
  //     })
  //   );
  if (entries.length > 0) {
    entries.forEach((name) => {
      let inputNode = document.querySelector(`form [name="${name}"]`);

      if (inputNode) {
        inputNode.classList.add("input-error");
        inputNode.onchange = (e) => inputNode.classList.remove("input-error");
        inputNode.addEventListener("change", () => {
          inputNode.classList.remove("input-error");
        });
      }
    });
  }
}

export function clearErrors(document, errors, name) {
  if (!errors) return;
  if (name) return (errors[name] = null);
  var entries = Object.keys(errors);
  if (entries.length > 0) {
    entries.forEach((name) => {
      let inputNode = document.querySelector(`form [name="${name}"]`);
      if (inputNode) {
        inputNode.classList.remove("input-error");
      }
    });
  }
}

export function numberKeyValidator(event) {
  let prevent = false;
  if (event.key === "e") prevent = true;
  if (event.key === ".") prevent = true;
  if (event.key === ",") prevent = true;
  prevent && event.preventDefault();
}

export const validateFileType = (formats) => (file) => {
  if (typeof file === "string") return true;
  if (!file) return;

  let type = file.type;
  return (formats ?? SUPPORTED_IMAGE_FORMATS).includes(type);
};

export const validateFileSize = (size) => (file) => {
  if (typeof file === "string") return true;
  if (!file) return;
  return (size ?? MAXFILE_SIZE) >= file.size;
};
