import DeleteIcon from "@mui/icons-material/Delete";
import IconButton from "@mui/material/IconButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";
import { request } from "../api";
import EditTwoToneIcon from '@mui/icons-material/EditTwoTone';
import PrimaryButton from "components/button/PrimaryButton";
import AddButton from "components/button/AddButton";
import { Modal,Box,Stack } from "@mui/material";
import TextField from "@mui/material/TextField";
function BuildingScreen() {
  
  const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 800,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

  const [buildings, setBuildings] = useState([]);
  const [isModalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    id: "",
    name: "",
    floors: "",
    location: "",
  });
  const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);
const [updateFormData, setUpdateFormData] = useState({
  id: "",
  name: "",
  floors: "",
  location: "",
});
const handleUpdate = (building) => {
  setUpdateFormData({
    id: building.id,
    name: building.name,
    floors: building.floors,
    location: building.location,
  });
  setUpdateModalOpen(true);
};
  const handleAddFunction = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };
  const handleUpdateConfirm = () => {
    console.log("Dữ liệu Form Cập Nhật:", updateFormData);
    request("put", `/buildings/update/${updateFormData.id}`,"success","error",updateFormData, (res) => {
      console.log(res);
      setBuildings(res.data);
    }).then();
    handleCloseUpdateModal();
  };
  const handleCloseUpdateModal = () => {
    setUpdateModalOpen(false);
  };
  const handleConfirm = () => {
    
    console.log("Dữ liệu Form:", formData);
    request("post", "/buildings/add","success","error",formData, (res) => {
      console.log(res);
      setBuildings(res.data);
    }).then();
   
    handleCloseModal();
  };
  const handleDelete = (buildingId) => {
    request("delete", `/buildings/delete/${buildingId}`, "success", "error", null, (res) => {
      console.log(res);
      setBuildings(res.data);
    }).then();
  };
  const handleUpdateInputChange = (field) => (event) => {
    setUpdateFormData({
      ...updateFormData,
      [field]: event.target.value,
    });
  };
  const handleInputChange = (field) => (event) => {
    setFormData({
      ...formData,
      [field]: event.target.value,
    });
  };
  useEffect(() => {
    console.log("call api");
    request("get", "/buildings/get-all", (res) => {
      
      setBuildings(res.data);
    }).then();
  }, []); //
  const columns = [
    {
      title: "Building",
      field: "id",
    },
    {
      title: "Name",
      field: "name",
    },
    {
      title: "Floors",
      field: "floors",
    },
    {
      title: "Location",
      field: "location",
    },
    {
      title: "Delete",
      sorting: false,
      render: (rowData) => (
        <IconButton
        onClick={() => {
          handleDelete(rowData.id);
        }}
          variant="contained"
          color="error"
        >
          <DeleteIcon />
        </IconButton>
      ),
    },
    {
      title: "Update",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            handleUpdate(rowData);
          }}
          variant="contained"
          color="primary"
        >
          <EditTwoToneIcon />
        </IconButton>
      ),
    },
  ];


 
  return (
    <div>
      <AddButton onClick={handleAddFunction} />
      <StandardTable
        title="Bulding List"
        columns={columns}
        data={buildings}
        // hideCommandBar
        //commandBarComponents={AddButton}
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
      />
      {/* Modal */}
      <Modal open={isModalOpen} onClose={handleCloseModal}>
        <Box sx={style} >
          <h2>Thêm Toà Nhà</h2>
          <Stack spacing={2}><TextField
            label="ID"
            value={formData.id}
            onChange={handleInputChange("id")}
          />
          <TextField
            label="Tên"
            value={formData.name}
            onChange={handleInputChange("name")}
          />
          <TextField
            label="Số Tầng"
            value={formData.floors}
            onChange={handleInputChange("floors")}
          />
          <TextField
            label="Vị Trí"
            value={formData.location}
            onChange={handleInputChange("location")}
          />
          <PrimaryButton onClick={handleConfirm}>Xác Nhận</PrimaryButton></Stack>
          
        </Box>
      </Modal>
      <Modal open={isUpdateModalOpen} onClose={handleCloseUpdateModal}>
  <Box sx={style}>
    <h2>Cập Nhật Toà Nhà</h2>
    <Stack spacing={2}>
      <TextField
        label="Tên"
        value={updateFormData.name}
        onChange={handleUpdateInputChange("name")}
      />
      <TextField
        label="Số Tầng"
        value={updateFormData.floors}
        onChange={handleUpdateInputChange("floors")}
      />
      <TextField
        label="Vị Trí"
        value={updateFormData.location}
        onChange={handleUpdateInputChange("location")}
      />
      <PrimaryButton onClick={handleUpdateConfirm}>Xác Nhận</PrimaryButton>
    </Stack>
  </Box>
</Modal>
    </div>
  );
}

export default BuildingScreen;
