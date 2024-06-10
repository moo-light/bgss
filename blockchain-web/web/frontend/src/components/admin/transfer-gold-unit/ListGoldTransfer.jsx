import { EditOutlined, SaveOutlined } from "@ant-design/icons";
import { Button, Flex, Input } from "antd";
import { useEffect, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import toast from "react-hot-toast";
import { useSearchParams } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
    useGetTransferGoldQuery,
    useUpdateTransferGoldMutation,
} from "../../../redux/api/transfersGoldApi";
import AdminLayout, { AdminLayoutLoader } from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const Yup = require("yup");
const CONVERSION_REGEX = /^(\d+(\.\d+)?)(\s*\/\s*\d+(\.\d+)?)?$/;

const testValues = [
  { value: "0.0321507", expected: true },
  { value: "12.2 / 32.2", expected: true },
  { value: "3.2", expected: true },
  { value: "1.233/3.2222", expected: true },
  { value: "1.2   /3.2", expected: true },
  { value: "1.2 /3.2", expected: true },
  { value: "1.2/ 3.2", expected: true },
  { value: "3.2 /", expected: false },
  { value: "1.2 / 3.2 / 4.2", expected: false },
];

testValues.forEach(({ value, expected }) => {
  const actual = CONVERSION_REGEX.test(value);
  console.assert(actual === expected, `Test failed for value: ${value}`);
});
// validations
const validationSchema = Yup.object().shape({
  conversionFactor: Yup.string()
    .required("value is required")
    .test(
      "is-decimal",
      "Values must be in double format with a decimal point or #.##/#.##",
      (value) => CONVERSION_REGEX.test(value) // Apply regex on the value
    ),
  // Add validation for other fields if necessary
});

//constants
const EDIT = "edits";
const SEPERATOR = ",";

// function

const convertToNumber = (v) => {
  try {
    const index = v.toString().indexOf("/");
    if (index !== -1) {
      const v1 = v.substring(0, index);
      const v2 = v.substring(index + 1);
      const value = parseFloat(v1) / parseFloat(v2);
      if (!isNaN(value)) return value;
    } else {
      v = v.parseFloat(v);
    }
  } catch (e) {}
  return v;
};

const ListGoldTransfer = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const { data, isLoading, ...other } = useGetTransferGoldQuery();
  const [updateConversion, { data: result, isSuccess, isError, error }] =
    useUpdateTransferGoldMutation();

  const [values, setValues] = useState({});
  const [errors, setErrors] = useState({});

  const title = "Gold Transfer ManagemenFt";

  useEffect(() => {
    if (other.isSuccess) {
      const newValues = [...data.data].reduce((current, obj) => {
        current[obj.id] = { ...obj };
        return current;
      }, {});
      setValues(newValues);
    }
  }, [other.isSuccess]);
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success(result.message);
    }
  }, [isSuccess, isError]);
  const handleClick = (id) => () => {
    const newEditList = [...editList, id].join(SEPERATOR).toString();
    searchParams.set(EDIT, newEditList);
    setSearchParams(searchParams);
  };
  const handleCancel = (id) => () => {
    const newEditList = editList
      .filter((el) => el !== id)
      .join(SEPERATOR)
      .toString();
    searchParams.set(EDIT, newEditList);
    setSearchParams(searchParams);
  };
  const handleSubmit = (value) => (e) => {
    e.preventDefault();
    validationSchema
      .validate(value)
      .then((v) => {
        setValues((prev) => {
          value.conversionFactor = String(
            convertToNumber(value.conversionFactor)
          );
          prev[value.id] = value;
          return prev;
        });
        updateConversion(value);
      })
      .catch((e) => setErrors({ ...errors, [value.id]: e.message }));
  };

  if (isLoading) {
    return <AdminLayoutLoader title={title}></AdminLayoutLoader>;
  }
  const handleChange = (id) => (e) => {
    const newValues = { ...values };

    newValues[id].conversionFactor = e.target.value;
    setValues(newValues);
    setErrors((prev) => {
      prev[id] = null;
      return prev;
    });
  };
  const actions = (id) => (
    <Flex gap="small">
      {editList.includes(id) ? (
        <>
          <OverlayTrigger
            overlay={
              <Tooltip>
                {convertToNumber(values[id]?.conversionFactor ?? "0")}
              </Tooltip>
            }
          >
            <Button
              className=""
              type="primary"
              htmlType="submit"
              form={`edit-${id}`}
            >
              Save <SaveOutlined />
            </Button>
          </OverlayTrigger>
          <Button className="" onClick={handleCancel(id)}>
            Cancel
          </Button>
        </>
      ) : (
        <Button className="" onClick={handleClick(id)}>
          Edit <EditOutlined />
        </Button>
      )}
    </Flex>
  );
  const editList = searchParams.get(EDIT)?.split(SEPERATOR) ?? [];
  return (
    <AdminLayout>
      <MetaData title={title} />
      <h1 className="my-2 px-5">Weight Conversion Information</h1>
      <div className="content my-3">
        <p>
          This table provides information about conversion factors for different
          weight units used in the context of gold trading.
        </p>
        <table className="table">
          <thead>
            <tr>
              <th className="col-auto">ID</th>
              <th className="col-2">From Unit</th>
              <th className="col-2">To Unit</th>
              <th className="col-2">Conversion Factor</th>
              <th className="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            {data?.data?.map((conversion) => (
              <tr key={conversion.id}>
                <td>{conversion.id}</td>
                <td>{conversion.fromUnit}</td>
                <td>{conversion.toUnit}</td>
                {!editList.includes(String(conversion.id)) ? (
                  <td>{conversion.conversionFactor}</td>
                ) : (
                  <td>
                    <Input
                      value={values[conversion.id]?.conversionFactor}
                      onChange={handleChange(conversion.id)}
                      name="conversionFactor"
                      aria-invalid={!!errors[conversion.id]}
                      form={`edit-${conversion.id}`}
                    />
                    <FormError
                      name={conversion.id}
                      form={`edit-${conversion.id}`}
                      errorData={errors}
                    />
                  </td>
                )}
                <td>
                  {actions(String(conversion.id))}
                  <form
                    className="d-none"
                    id={`edit-${conversion.id}`}
                    onSubmit={handleSubmit(values[conversion.id])}
                  >
                    <input type="hidden" value={conversion.id} />
                  </form>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  );
};
export default ListGoldTransfer;
