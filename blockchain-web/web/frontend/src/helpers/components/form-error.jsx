export const FormError = ({ errorData = {}, name, touched = {}, form }) => {
  if (errorData[name]?.length <= 0 && !touched[name]) return <></>;

  return (
    <span className={`form-error`} >
      {errorData[name]}
    </span>
  );
};
