export function userColors(v) {
  switch (v) {
    case "ACTIVE":
      return "greenColor";
    case "INACTIVE":
      return "redColor";
    case "VERIFIED":
      return "gold";
    case "UNVERIFIED":
      return "text";
    default:
      return "text";
  }
}

export function orderColors(v) {
  switch (v) {
    case "RECIEVED":
      return "greenColor";
    case "NOT_RECIEVED":
      return "yellowColor";
    case "UNVERIFIED":
      return "text-50";
    default:
      return "text";
  }
}
export function transactionColors(v) {
  switch (v) {
    case "CONFIRMED":
    case "COMPLETED":
      return "greenColor";
    case "REJECTED":
      return "redColor";
    case "VERIFIED":
      return "gold";
    case "PENDING":
      return "yellowColor";
    case "UNVERIFIED":
      return "text";
    default:
      return "text";
  }
}
export function withdrawColors(v) {
  switch (v) {
    case "COMPLETED":
    case "CONFIRMED":
      return "greenColor";
    case "CANCELED":
      return "redColor";
    case "PENDING":
      return "yellowColor";
    case "UNVERIFIED":
      return "text";
    default:
      return "text";
  }
}

export function simpleYesNoColors(v) {
  switch (v) {
    case true:
      return "greenColor";
    case false:
      return "redColor";
    default:
      return "text";
  }
}
