import { Button, Modal } from "react-bootstrap";
import { useGetTransferGoldQuery } from "../../redux/api/transfersGoldApi";

function WeightConversionModal({ show, handleClose }) {
  const { data, error, isLoading } = useGetTransferGoldQuery();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error fetching data</div>;
  }

  return (
    <Modal show={show} onHide={handleClose} size={"xl"}>
      <div className="modal-content" style={{ width: "100%" }}>
        <Modal.Header closeButton>
          <Modal.Title>Weight Conversion Information</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            This table provides information about conversion factors for
            different weight units used in the context of gold trading.
          </p>
          <table className="table">
            <thead>
              <tr>
                <th>From Unit</th>
                <th>To Unit</th>
                <th>Conversion Factor</th>
              </tr>
            </thead>
            <tbody>
              {data?.data?.map((conversion) => (
                <tr key={conversion.id}>
                  <td>{conversion.fromUnit}</td>
                  <td>{conversion.toUnit}</td>
                  <td>{conversion.conversionFactor}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </div>
    </Modal>
  );
}

export default WeightConversionModal;

// import { Button, Modal } from "react-bootstrap";
// import {
//   useGetTransferGoldQuery,
// } from "../../redux/api/typeGoldApi";
// function WeightConversionModal({ show, handleClose }) {
//   return (
//     <Modal show={show} onHide={handleClose} size={"xl"}>
//       <Modal.Header closeButton>
//         <Modal.Title>Weight Conversion Information</Modal.Title>
//       </Modal.Header>
//       <Modal.Body>
//         <p>
//           This table provides information about conversion factors for different
//           weight units used in the context of gold trading.
//         </p>
//         <table className="table">
//           <thead>
//             <tr>
//               <th>From Unit</th>
//               <th>To Unit</th>
//               <th>Conversion Factor</th>
//             </tr>
//           </thead>
//           <tbody>
//             {/* Troy Ounce */}
//             <tr>
//               <td>Troy Ounce (tOz)</td>
//               <td>Gram (g)</td>
//               <td>1 / 0.0321507 = 31.1034768</td>
//             </tr>
//             <tr>
//               <td>Troy Ounce (tOz)</td>
//               <td>Mace</td>
//               <td>1 / 0.1214655 = 8.24000108</td>
//             </tr>
//             <tr>
//               <td>Troy Ounce (tOz)</td>
//               <td>Tael</td>
//               <td>1 / 1.21528 = 0.822855638</td>
//             </tr>
//             <tr>
//               <td>Troy Ounce (tOz)</td>
//               <td>Kilogram (Kg)</td>
//               <td>1 / 32.1507 = 0.0311034768</td>
//             </tr>
//             {/* Gram */}
//             <tr>
//               <td>Gram (g)</td>
//               <td>Troy Ounce (tOz)</td>
//               <td>0.0321507</td>
//             </tr>
//             <tr>
//               <td>Gram (g)</td>
//               <td>Mace</td>
//               <td>0.0321507 / 0.1214655 = 0.2645833309</td>
//             </tr>
//             <tr>
//               <td>Gram (g)</td>
//               <td>Tael</td>
//               <td>0.0321507 / 1.21528 = 0.0264345385</td>
//             </tr>
//             <tr>
//               <td>Gram (g)</td>
//               <td>Kilogram (Kg)</td>
//               <td>0.0321507 / 32.1507 = 0.001</td>
//             </tr>
//             {/* Mace */}
//             <tr>
//               <td>Mace</td>
//               <td>Troy Ounce (tOz)</td>
//               <td>0.1214655</td>
//             </tr>
//             <tr>
//               <td>Mace</td>
//               <td>Gram (g)</td>
//               <td>0.1214655 / 0.0321507 = 3.779936417</td>
//             </tr>
//             <tr>
//               <td>Mace</td>
//               <td>Tael</td>
//               <td>0.1214655 / 1.21528 = 0.100000834</td>
//             </tr>
//             <tr>
//               <td>Mace</td>
//               <td>Kilogram (Kg)</td>
//               <td>0.1214655 / 32.1507 = 0.0037759793</td>
//             </tr>
//             {/* Tael */}
//             <tr>
//               <td>Tael</td>
//               <td>Troy Ounce (tOz)</td>
//               <td>1.21528</td>
//             </tr>
//             <tr>
//               <td>Tael</td>
//               <td>Gram (g)</td>
//               <td>1.21528 / 0.0321507 = 37.799364167</td>
//             </tr>
//             <tr>
//               <td>Tael</td>
//               <td>Mace</td>
//               <td>1.21528 / 0.1214655 = 10.00001114</td>
//             </tr>
//             <tr>
//               <td>Tael</td>
//               <td>Kilogram (Kg)</td>
//               <td>1.21528 / 32.1507 = 0.03779936417</td>
//             </tr>
//             {/* Kilogram (Kg) */}
//             <tr>
//               <td>Kilogram (Kg)</td>
//               <td>Troy Ounce (tOz)</td>
//               <td>32.1507</td>
//             </tr>
//             <tr>
//               <td>Kilogram (Kg)</td>
//               <td>Gram (g)</td>
//               <td>32.1507 / 0.0321507 = 1000</td>
//             </tr>
//             <tr>
//               <td>Kilogram (Kg)</td>
//               <td>Mace</td>
//               <td>32.1507 / 0.1214655 = 264.5833309</td>
//             </tr>
//             <tr>
//               <td>Kilogram (Kg)</td>
//               <td>Tael</td>
//               <td>32.1507 / 1.21528 = 26.4345385</td>
//             </tr>
//           </tbody>
//         </table>
//       </Modal.Body>
//       <Modal.Footer>
//         <Button variant="secondary" onClick={handleClose}>
//           Close
//         </Button>
//       </Modal.Footer>
//     </Modal>
//   );
// }
// export default WeightConversionModal;
