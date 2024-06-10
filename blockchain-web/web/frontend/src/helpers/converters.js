export const GOLD_UNIT_CONVERT = {
  KG: "KILOGRAM",
  G: "GRAM",
  TAEL: "TAEL",
  MACE: "MACE",
  TOZ: "TROY_OZ",
};
export const GOLD_UNIT_CONVERT_2 = Object.assign(
  {},
  ...Object.keys(GOLD_UNIT_CONVERT).map((b) => ({ [GOLD_UNIT_CONVERT[b]]: b }))
);
// export let conversionFactors = {
//   tOz: {
//     tOz: 1,
//     g: 1 / 0.0321507,
//     Mace: 1 / 0.1214655,
//     Tael: 1 / 1.21528,
//     Kg: 1 / 32.1507,
//   },
//   g: {
//     tOz: 0.0321507,
//     Mace: 0.0321507 / 0.1214655,
//     Tael: 0.0321507 / 1.21528,
//     Kg: 0.0321507 / 32.1507,
//     g: 1,
//   },
//   Mace: {
//     tOz: 0.1214655,
//     g: 0.1214655 / 0.0321507,
//     Tael: 0.1214655 / 1.21528,
//     Kg: 0.1214655 / 32.1507,
//     Mace: 1,
//   },
//   Tael: {
//     tOz: 1.21528,
//     g: 1.21528 / 0.0321507,
//     Mace: 1.21528 / 0.1214655,
//     Kg: 1.21528 / 32.1507,
//     Tael: 1,
//   },
//   Kg: {
//     tOz: 32.1507,
//     g: 32.1507 / 0.0321507,
//     Mace: 32.1507 / 0.1214655,
//     Tael: 32.1507 / 1.21528,
//     Kg: 1,
//   }
// };
