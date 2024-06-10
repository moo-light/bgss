import { FormError } from "./form-error";

function FormSelect({
  label,
  name,
  values = {},
  errors = {},
  options = [],
  touched = {},
  className = "",
  onChange,
  unselect = false,
  leading = null,
  trailing = null,
  type = "text",
  ...others
}) {
  return (
    <div className={`mb-3 form-group ${className}`}>
      <label className="form-label">{label}</label>
      <div className="input-group w-100">
        {leading && <span className="input-group-text">{leading}</span>}
        <select
          type={type}
          className={`form-control`}
          name={name}
          aria-invalid={!!errors[name]}
          value={values[name]}
          onChange={onChange}
          {...others}
        >
          {unselect && <option value={null} />}
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {trailing && <span className="input-group-text">{trailing}</span>}
      </div>
      <FormError name={name} errorData={errors} touched={touched} />
    </div>
  );
}
export default FormSelect;
