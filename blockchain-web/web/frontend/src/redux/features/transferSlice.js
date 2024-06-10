import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  conversionFactors: {},
};

export const transferSlice = createSlice({
  initialState,
  name: "transferSlice",
  reducers: {
    createConversionFactors: (state, { payload }) => {
      function toSymbol(unit) {
        return payload.find((p) => p.fromUnit === unit).symbol;
      }

      const data = payload;
      const conversionFactors = state.conversionFactors;
      data.forEach(({ fromUnit, toUnit, conversionFactor }) => {
        fromUnit = toSymbol(fromUnit);
        toUnit = toSymbol(toUnit);
        // Ensure the conversion factor is a number
        conversionFactor = parseFloat(conversionFactor);
        // Initialize the fromUnit object if it doesn't exist
        if (!conversionFactors[fromUnit]) {
          conversionFactors[fromUnit] = { [fromUnit]: 1 };
        }
        // Add the conversion factor for the toUnit
        conversionFactors[fromUnit][toUnit] = conversionFactor;
      });
      // cf = conversionFactors;
      state.conversionFactors = conversionFactors;
    },
  },
});
export function weightConverter(number, t1, t2, conversionFactors) {
  if (t1 === t2) return Number(number);
  if (
    conversionFactors.hasOwnProperty(t1) &&
    conversionFactors[t1].hasOwnProperty(t2)
  ) {
    return Math.round(Number(number) * conversionFactors[t1][t2] * 100) / 100;
  } else {
    console.error("Invalid weight");
  }
}
export default transferSlice.reducer;
export const { createConversionFactors } = transferSlice.actions;
