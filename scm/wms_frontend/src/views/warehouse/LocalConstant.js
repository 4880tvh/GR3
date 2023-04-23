import { Button } from "@mui/material";

export const staticProductFields = [
  {
    field: "name",
    headerName: "Tên sản phẩm",
    sortable: false,
    minWidth: 200,
    valueGetter: (params) => {
      return params?.row?.product?.name;
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 100,
  },
  {
    field: "inventoryQty",
    headerName: "Còn trong kho",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "qtyThreshold",
    headerName: "Ngưỡng tồn kho",
    sortable: false,
    minWidth: 150,
  },
];
export const staticDatagridCols = [
  {
    field: "code",
    headerName: "Mã code",
    sortable: false,
    pinnable: true,
    minWidth: 150,
  },
  {
    field: "name",
    headerName: "Tên kho",
    sortable: false,
    minWidth: 150,
  },
  {
    field: "createdBy",
    headerName: "Người tạo",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.creator?.id;
    },
  },
  {
    field: "address",
    headerName: "Địa chỉ",
    sortable: false,
    pinnable: true,
  },
  {
    field: "managedBy",
    headerName: "Thủ kho",
    sortable: false,
    minWidth: 150,
    valueGetter: (params) => {
      return params?.row?.manager?.name
        ? params?.row?.manager?.name
        : "Chưa có";
    },
  },
  {
    field: "status",
    headerName: "Trạng thái",
    sortable: false,
    minWidth: 150,
    renderCell: (params) => {
      return (
        <Button variant="outlined" color="info">
          {params?.row?.status}
        </Button>
      );
    },
  },
];
