import * as React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

// AlertDialog.js
export default function AlertDialog({ open, handleClose, title, description, agreeText, disagreeText, onAgree }) {
  return (
    <Dialog
      open={open}
      onClose={handleClose}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
    >
      <DialogTitle id="alert-dialog-title">{title}</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">
          {description}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>{disagreeText || 'Disagree'}</Button>
        <Button onClick={() => { handleClose(); if (onAgree) onAgree(); }} autoFocus>
          {agreeText || 'Agree'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
