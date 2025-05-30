package main.app.buttons.edit;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.app.errors.EmptyInputException;
import main.app.errors.ExistingCodeException;
import main.app.errors.ExistingNameException;
import main.app.errors.MultiEditCollegeException;
import main.app.errors.NoRowSelectedException;
import main.app.input.CollegeInput;
import main.app.input.InputType;
import main.app.tables.ManagementTable;
import main.app.windows.MainFrame;

/**
 * {@code JButton} class that sets up all components needed to edit at
 * least one college's data in the database based on the selected
 * row(s) in {@link ManagementTable}.
 * @see CollegeInput {@code CollegeInput}
 */
public class EditCollegeButton extends EditDataButton{
    private CollegeInput clgInput;
    public EditCollegeButton(MainFrame mainFrame, ManagementTable mTable) {
        super(mainFrame, mTable);
        this.setDataText("College");
        this.setText(this.getActionDataText());
        this.setVisible(false);
    }

    @Override
    protected void setUpComponents(ManagementTable mTable) throws NoRowSelectedException, MultiEditCollegeException {
        this.getDataDialog().setTitle("Edit College");
        if(mTable.getSelectedRowCount() <= 1){
            clgInput = new CollegeInput(this.getDataDialog(), mTable, this.getGBC(), InputType.EDIT_SINGLE);
        } else {
            throw new MultiEditCollegeException();
        }
        
        this.getActionButton().setText("Edit College");
        this.getActionButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                editData(mTable); 
            }
        });

        //Adding Components

        this.getGBC().gridx = 0; this.getGBC().gridy = 2; this.getGBC().gridwidth = 3; 
        this.getGBC().fill = GridBagConstraints.HORIZONTAL;
        this.getDataDialog().add(this.getActionButton(),this.getGBC());

    }

    @Override
    protected void editData(ManagementTable mTable) {
        try{
            int row = mTable.getSelectedRow();
            String tableName = mTable.getCTM().getTableName();
            if(mTable.getdBDriver().ifRecordExists(mTable.getColumnName(0), tableName,clgInput.getCode())
            && !mTable.getValueAt(row, 0).equals(clgInput.getCode())){
                throw new ExistingCodeException();
            } else if (mTable.getdBDriver().ifRecordExists(mTable.getColumnName(0), tableName,clgInput.getName())
                && !mTable.getValueAt(row, 1).equals(clgInput.getName())){
                throw new ExistingNameException();
            } 
            String[] data = {
                clgInput.getCode(),
                clgInput.getName()
            };
            boolean confirm = JOptionPane.showConfirmDialog(getActionButton(), 
                                "Please confirm that the details of the college" 
                                + " that will be edited are correct.", 
                                "Confirm Editing College", 
                                JOptionPane.YES_NO_OPTION) 
                                == JOptionPane.YES_OPTION;
            if(confirm){
                mTable.getCTM().editData(mTable.convertRowIndexToModel(row), data);
                JOptionPane.showMessageDialog(getActionButton(), "College edited successfully!");
                getDataDialog().dispose();
            }
        } catch(EmptyInputException | ExistingCodeException | ExistingNameException e){
            e.printStackTrace();
            e.startErrorWindow(getActionButton());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
