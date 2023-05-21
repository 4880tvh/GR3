import { Box, FormControl, InputLabel, MenuItem, Select, TextField, Typography, Autocomplete, Divider, Switch, Alert, AlertTitle } from "@mui/material";
import PrimaryButton from "components/button/PrimaryButton";
import TertiaryButton from "components/button/TertiaryButton";
import CustomizedDialogs from "components/dialog/CustomizedDialogs";
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { DemoContainer } from '@mui/x-date-pickers/internals/demo';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { AnimatePresence, motion } from "framer-motion";
import { grey } from "@mui/material/colors";
import React, { useContext, useEffect, useState } from "react";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { useKeycloak } from "@react-keycloak/web";
import { MyContext } from "contextAPI/MyContext";
import './styles.scss';
import { createShipment } from "api/ShipmentAPI";

const NewShipmentModal = ({ open, setOpen, setToast, setToastType }) => {
    const [description, setDescription] = useState("");
    const [executedTime, setExecutedTime] = useState();
    const { preferred_username } = useContext(MyContext);

    const handleClose = () => {
        setOpen(false);
    }
    const handleSubmit = () => {
        let dataSubmit = {
            created_by_user_id: preferred_username,
            description: description,
            executed_time: executedTime
        }
        createShipment(dataSubmit).then((res) => {
            console.log("res", res)
            if(!res) {
                setToastType("error");
            } else {
                setToastType("success");
            }
            handleClose();
            setToast(true);
            setTimeout(() => {
                setToast(false);
            }, "2000");
        })
    }
    return (
        <Box>
            <CustomizedDialogs
                open={open}
                handleClose={handleClose}
                contentTopDivider
                title="New Shipment"
                className="modalOrder"
                content={
                    <AnimatePresence>
                        <motion.div>
                            <motion.div
                                animate="center"
                                transition={{
                                    opacity: { duration: 0.1 },
                                }}
                            >
                                <Box pt="7px" pb={1} pl={1} pr={1} sx={{ width: "650px" }} className="contentModalShipment">
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Executed Time:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <LocalizationProvider dateAdapter={AdapterDayjs}>
                                                <DemoContainer components={['DateTimePicker']}>
                                                    <DateTimePicker label="Executed Time"
                                                        onChange={(e) => setExecutedTime((new Date(e)).getTime())} />
                                                </DemoContainer>
                                            </LocalizationProvider>
                                        </Box>
                                    </Box>
                                    <Box className="contentModal-item">
                                        <Box className="contentModal-item-text">
                                            <Typography>Description:</Typography>
                                        </Box>
                                        <Box className="contentModal-item-input">
                                            <TextField
                                                id="outlined-textarea"
                                                label="Description"
                                                placeholder="description"
                                                multiline
                                            />
                                        </Box>
                                    </Box>
                                    <Divider />
                                    <Box
                                        display="flex"
                                        justifyContent="flex-end"
                                        pt={2} ml={-1} mr={-1}
                                    >
                                        <TertiaryButton
                                            onClick={() => setOpen(false)}
                                            sx={{ margin: "4px 8px" }}
                                            style={{ width: 100 }}
                                        >
                                            Cancel
                                        </TertiaryButton>
                                        <PrimaryButton
                                            // disabled={
                                            //     isEmpty(feature) || isEmpty(detail) || isSubmitting
                                            // }
                                            sx={{ margin: "4px 8px" }}
                                            style={{ width: 100 }}
                                            onClick={handleSubmit}
                                        >
                                            {false ? "Đang gửi..." : "Submit"}
                                        </PrimaryButton>
                                    </Box>
                                </Box>
                            </motion.div>
                        </motion.div>
                    </AnimatePresence>
                }
            />
        </Box>
    )
}
export default NewShipmentModal;