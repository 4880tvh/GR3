import Button from "@mui/material/Button";
import { green } from "@mui/material/colors";
import { styled } from "@mui/material/styles";

const StyledButton = styled(Button)(({ theme }) => ({
  textTransform: "none",
  backgroundColor: green[300],
  color: theme.palette.getContrastText(green[700]),
  "&:hover": {
    backgroundColor: green[700],
  },
}));

const AddButton = (props) => (
  <StyledButton variant="contained" {...props}>
    {props.children}AddButton
  </StyledButton>
);

export default AddButton;
