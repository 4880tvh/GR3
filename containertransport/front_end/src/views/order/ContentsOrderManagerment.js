import * as React from 'react';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import TableSortLabel from '@mui/material/TableSortLabel';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Checkbox from '@mui/material/Checkbox';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import DeleteIcon from '@mui/icons-material/Delete';
import FilterListIcon from '@mui/icons-material/FilterList';
import { visuallyHidden } from '@mui/utils';
import './styles.scss';
import { Button, Chip, Divider, Icon, Menu, MenuItem } from '@mui/material';
import { colorStatus, menuIconMap, roles, typeOrderMap } from 'config/menuconfig';
import { useHistory } from 'react-router-dom';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import EditIcon from '@mui/icons-material/Edit';
import { deleteOrder, updateOrderList } from 'api/OrderAPI';
import { MyContext } from 'contextAPI/MyContext';

function descendingComparator(a, b, orderBy) {
    if (b[orderBy] < a[orderBy]) {
        return -1;
    }
    if (b[orderBy] > a[orderBy]) {
        return 1;
    }
    return 0;
}

function getComparator(order, orderBy) {
    return order === 'desc'
        ? (a, b) => descendingComparator(a, b, orderBy)
        : (a, b) => -descendingComparator(a, b, orderBy);
}

function stableSort(array, comparator) {
    const stabilizedThis = array.map((el, index) => [el, index]);
    stabilizedThis.sort((a, b) => {
        const order = comparator(a[0], b[0]);
        if (order !== 0) {
            return order;
        }
        return a[1] - b[1];
    });
    return stabilizedThis.map((el) => el[0]);
}

const headCells = [
    {
        id: 'code',
        numeric: false,
        disablePadding: true,
        label: 'Order Code',
        with: '12%'
    },
    {
        id: 'customer',
        numeric: false,
        disablePadding: true,
        label: 'Customer',
        with: '8%'
    },
    {
        id: 'fromFacility',
        numeric: false,
        disablePadding: true,
        label: 'From Facility',
        with: '12%'
    },
    {
        id: 'toFacility',
        numeric: false,
        disablePadding: true,
        label: 'To Facility',
        with: '12%'
    },
    {
        id: 'type',
        numeric: false,
        disablePadding: false,
        label: 'Type',
        with: '13%'
    },
    {
        id: 'status',
        numeric: false,
        disablePadding: false,
        label: 'Status',
        with: '18%'
    },
    {
        id: 'createdAt',
        numeric: false,
        disablePadding: false,
        label: 'Created At',
        with: '13%'
    },
    {
        id: 'updateAt',
        numeric: false,
        disablePadding: false,
        label: 'Updated At',
        with: '13%'
    },
    {
        id: 'view',
        numeric: false,
        disablePadding: true,
        label: '',
    },
];

const DEFAULT_ORDER = 'asc';
const DEFAULT_ORDER_BY = 'calories';

function EnhancedTableHead(props) {
    const { onSelectAllClick, order, orderBy, numSelected, rowCount, onRequestSort } = props;
    const createSortHandler = (newOrderBy) => (event) => {
        onRequestSort(event, newOrderBy);
    };

    return (
        <TableHead>
            <TableRow>
                <TableCell padding="checkbox">
                    <Checkbox
                        color="primary"
                        indeterminate={numSelected > 0 && numSelected < rowCount}
                        checked={rowCount > 0 && numSelected === rowCount}
                        onChange={onSelectAllClick}
                        inputProps={{
                            'aria-label': 'select all desserts',
                        }}
                    />
                </TableCell>
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        align={headCell.numeric ? 'right' : 'left'}
                        padding={headCell.disablePadding ? 'none' : 'normal'}
                        sortDirection={orderBy === headCell.id ? order : false}
                        width={headCell.with}
                    >
                        <TableSortLabel
                            active={orderBy === headCell.id}
                            direction={orderBy === headCell.id ? order : 'asc'}
                            onClick={createSortHandler(headCell.id)}
                        >
                            {headCell.label}
                            {orderBy === headCell.id ? (
                                <Box component="span" sx={visuallyHidden}>
                                    {order === 'desc' ? 'sorted descending' : 'sorted ascending'}
                                </Box>
                            ) : null}
                        </TableSortLabel>
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
}

export default function ContentsOrderManagerment({ orders, page, setPage, rowsPerPage, setRowsPerPage, count, flag, setFlag, type, setToast, setToastType, setToastMsg}) {
    const [order, setOrder] = React.useState(DEFAULT_ORDER);
    const [orderBy, setOrderBy] = React.useState(DEFAULT_ORDER_BY);
    const [selected, setSelected] = React.useState([]);
    const [dense, setDense] = React.useState(false);
    const [visibleRows, setVisibleRows] = React.useState(null);
    const history = useHistory();

    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const [openButton, setOpenButton] = React.useState(false);
    const [status, setStatus] = React.useState('');

    const { role, preferred_username } = React.useContext(MyContext);

    const handleRequestSort = React.useCallback(
        (event, newOrderBy) => {
            const isAsc = orderBy === newOrderBy && order === 'asc';
            const toggledOrder = isAsc ? 'desc' : 'asc';
            setOrder(toggledOrder);
            setOrderBy(newOrderBy);

            const sortedRows = stableSort(orders, getComparator(toggledOrder, newOrderBy));
            const updatedRows = sortedRows.slice(
                page * rowsPerPage,
                page * rowsPerPage + rowsPerPage,
            );

            setVisibleRows(updatedRows);
        },
        [order, orderBy, page, rowsPerPage],
    );

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = orders.map((n) => n.uid);
            setSelected(newSelected);
            setOpenButton(true);
            return;
        }
        setSelected([]);
        setOpenButton(false);
    };

    const handleClick = (event, id) => {
        const selectedIndex = selected.indexOf(id);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selected.slice(0, selectedIndex),
                selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        const updatedRowsPerPage = parseInt(event.target.value, 10);
        setRowsPerPage(updatedRowsPerPage);

        setPage(0);
    };

    const isSelected = (name) => selected.indexOf(name) !== -1;
    const handleDetail = (uid) => {
        if (type === "WaitApprove") {
            history.push(`/wait-approve/order/${type}/${uid}`)
        }
        else {
            history.push(`/order/${uid}`)
        }
    }
    const handleClickAction = (event) => {
        setAnchorEl(event.currentTarget);
    };
    const handleCloseAction = () => {
        setAnchorEl(null);
    };
    const handleChangeAction = (value) => {
        setAnchorEl(null);
        setStatus(value);
        let data = {
            status: value,
            uidList: selected
        }
        updateOrderList(data).then((res) => {
            console.log("res", res);
            setFlag(!flag);
        })
    };
    const handleCancel = (uid) => {
        deleteOrder(uid).then((res) => {
            console.log(res);
            setToastMsg("Delete Order Success");
            setToastType("success");
            setToast(true);
            setFlag(!flag);
            setTimeout(() => {
                setToast(false);
            }, "3000");
        })
    }
    return (
        <Box sx={{ width: '100%', display: "flex", justifyContent: "center", backgroundColor: "white" }}>
            <Paper sx={{ width: '95%', mb: 2, boxShadow: "none" }}>
                {type === "WaitApprove" && selected.length > 0 ? (
                    <Box sx={{ marginTop: '8px' }}>
                        <Button
                            id="demo-customized-button"
                            aria-controls={open ? 'demo-customized-menu' : undefined}
                            aria-haspopup="true"
                            aria-expanded={open ? 'true' : undefined}
                            variant="contained"
                            disableElevation
                            onClick={handleClickAction}
                            endIcon={<KeyboardArrowDownIcon />}
                        >
                            Action
                        </Button>
                        <Menu
                            id="demo-customized-menu"
                            MenuListProps={{
                                'aria-labelledby': 'demo-customized-button',
                            }}
                            anchorEl={anchorEl}
                            open={open}
                            onClose={handleCloseAction}
                        >
                            <MenuItem
                                onClick={() => handleChangeAction("ORDERED")}
                            >
                                Approve
                            </MenuItem>
                            <MenuItem
                                onClick={() => handleChangeAction("REJECT")}   
                            >
                                Reject
                            </MenuItem>
                        </Menu>
                    </Box>
                ) : null}
                <TableContainer>
                    <Table
                        sx={{ minWidth: 750 }}
                        aria-labelledby="tableTitle"
                        size={dense ? 'small' : 'medium'}
                    >
                        <EnhancedTableHead
                            numSelected={selected.length}
                            order={order}
                            orderBy={orderBy}
                            onSelectAllClick={handleSelectAllClick}
                            onRequestSort={handleRequestSort}
                            rowCount={orders?.length}
                        />
                        <TableBody>
                            {orders
                                ? orders.map((row, index) => {
                                    const isItemSelected = isSelected(row.uid);
                                    const labelId = `enhanced-table-checkbox-${index}`;

                                    return (
                                        <TableRow
                                            hover
                                            // onClick={(event) => handleClick(event, row.name)}
                                            role="checkbox"
                                            aria-checked={isItemSelected}
                                            tabIndex={-1}
                                            key={row.id}
                                            selected={isItemSelected}
                                            sx={{ cursor: 'pointer' }}
                                        >
                                            <TableCell padding="checkbox">
                                                <Checkbox
                                                    color="primary"
                                                    checked={isItemSelected}
                                                    inputProps={{
                                                        'aria-labelledby': labelId,
                                                    }}
                                                    onClick={(event) => handleClick(event, row.uid)}
                                                />
                                            </TableCell>
                                            <TableCell
                                                component="th"
                                                id={labelId}
                                                scope="row"
                                                align="left"
                                            >
                                                {row?.orderCode}
                                            </TableCell>
                                            <TableCell align="left" sx={{ paddingLeft: '0px !important' }}>{row?.customerId}</TableCell>
                                            <TableCell align="left">{row?.fromFacility.facilityName}</TableCell>
                                            <TableCell align="left">{row?.toFacility.facilityName}</TableCell>
                                            <TableCell align="left">{typeOrderMap.get(row.type)}</TableCell>
                                            <TableCell align="left">
                                                <Chip label={row?.status} color={colorStatus.get(row?.status)} />
                                            </TableCell>
                                            <TableCell align="left">{new Date(row.createdAt).toLocaleDateString()}</TableCell>
                                            <TableCell align="left">{new Date(row.updatedAt).toLocaleDateString()}</TableCell>
                                            <TableCell >
                                                <Box sx={{ display: 'flex' }}>
                                                    <Tooltip title="View">
                                                        <Box
                                                            onClick={() => { handleDetail(row?.uid) }}
                                                        >
                                                            <Icon className='icon-view-screen'>{menuIconMap.get("RemoveRedEyeIcon")}</Icon>
                                                        </Box>
                                                    </Tooltip>
                                                    {(row?.status === "WAIT_APPROVE" && role.includes(roles.get("Customer"))) ? (
                                                        <Tooltip title="Delete">
                                                            <Box onClick={handleCancel}>
                                                                <Icon className='icon-view-screen' sx={{ marginLeft: '8px' }}>{menuIconMap.get("DeleteForeverIcon")}</Icon>
                                                            </Box>
                                                        </Tooltip>
                                                    ) : null}
                                                </Box>
                                            </TableCell>
                                        </TableRow>
                                    );
                                })
                                : null}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={count}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </Box>
    );

}

