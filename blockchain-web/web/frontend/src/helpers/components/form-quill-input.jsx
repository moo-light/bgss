import DOMPurify from "dompurify";
import { useState } from "react";
import { Button, Modal } from "react-bootstrap";
import { FaEye } from "react-icons/fa";
import ReactQuill from "react-quill";
import { FormError } from "./form-error";
import { clearErrors } from "./form-input";

function TextInput({
  label,
  name,
  values,
  errors,
  touched,
  className = "",
  onChange,
  formik = {},
  ...others
}) {
  const handleChange = (v) => {
    if (v.replace(/<(.|\n)*?>/g, "").trim().length === 0) {
      v = "";
    }
    formik.setFieldValue(name, v);
    onChange({ target: { name: name, value: v } });
    clearErrors(formik, name, errors);
  };
  //Modal Box
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  return (
    <div className={`mb-3 form-group ${className}`}>
      <label className="form-label">Description</label>
      <ReactQuill
        rows="8"
        className={`${!!errors[name] && "input-error"} rounded`}
        name="content"
        placeholder="Add Some Text"
        value={values[name]}
        onChange={handleChange}
      />
      <div className="d-flex justify-content-between align-items-start gap-2 mt-2 ">
        <FormError name="description" errorData={errors} />
        <button
          type="button"
          className="btn btn-primary m-0"
          onClick={(e) => setShow(true)}
          title={`View ${name}`}
        >
          <FaEye></FaEye>
        </button>
      </div>
      <Modal show={show} onHide={handleClose} size={"lg"}>
        <Modal.Header closeButton>
          <Modal.Title>{label}</Modal.Title>
        </Modal.Header>
        <div className="modal-body">
          {/* content */}
          <pre
            className="my-2 d-block "
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(values[name]),
            }}
          />
        </div>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
export default TextInput;
