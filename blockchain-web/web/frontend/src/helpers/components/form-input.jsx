import { FormError } from "./form-error";

function FormInput({
  label,
  name,
  values,
  errors,
  touched,
  className = "",
  onChange,
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
        <input
          type={type}
          className={`form-control`}
          name={name}
          aria-invalid={!!errors[name]}
          value={values[name]}
          onChange={onChange}
          {...others}
        />
        {trailing && <span className="input-group-text">{trailing}</span>}
      </div>
      <FormError name={name} errorData={errors} touched={touched} />
    </div>
  );
}
export default FormInput;

export const clearErrors = (formik, name, errors) => {
  if (!formik) return;
  formik.setErrors({ [name]: null, ...errors });
};
