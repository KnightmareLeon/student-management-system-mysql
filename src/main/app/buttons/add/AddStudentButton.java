package main.app.buttons.add;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import main.app.errors.EmptyInputException;
import main.app.errors.EmptyTableException;
import main.app.errors.ExistingIDException;
import main.app.errors.NoRowSelectedException;
import main.app.errors.NullPointerExceptionWithWindow;
import main.app.input.InputType;
import main.app.input.StudentInput;
import main.app.tables.ManagementTable;
import main.app.windows.MainFrame;

/**
 * {@code JButton} class that sets up all components needed to add a new
 * student to the database.
 * @see StudentInput {@code StudentInput}
 */
public class AddStudentButton extends AddDataButton{
    public AddStudentButton(MainFrame mainFrame, ManagementTable mTable){
        super(mainFrame, mTable);
        this.setDataText("Student");
        this.setText(this.getActionDataText());
        this.setVisible(true);
    }
    
    @Override
    protected void setUpComponents(ManagementTable mTable) throws NoRowSelectedException, EmptyTableException{
        this.getDataDialog().setTitle("Add Student");

        StudentInput stdInput = new StudentInput(this.getDataDialog(), mTable, this.getGBC(), InputType.ADD);
        
        //Add Student
        this.getActionButton().setText("Add Student");
        this.getActionButton().addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                try{
                    String tableName = mTable.getSTM().getTableName();
                    boolean confirm = true;
                    if(mTable.getdBDriver().ifRecordExists(mTable.getColumnName(0), 
                    tableName, stdInput.getID())){
                        throw new ExistingIDException();
                    }

                    String[] data = {
                        stdInput.getID(),
                        stdInput.getFN(),
                        stdInput.getLN(),
                        stdInput.getYL(),
                        stdInput.getG(),
                        stdInput.getPC()
                    };

                    if(mTable.getdBDriver().ifRecordExists(
               "FirstName", 
               "LastName", 
                            tableName, 
                            stdInput.getFN(), 
                            stdInput.getLN()))
                    {
                        confirm = (JOptionPane.showConfirmDialog(
                                    getActionButton(), 
                            "Name already exists. Do you want to proceed?", 
                              "Same Name Confirmation", 
                                    JOptionPane.YES_NO_OPTION) 
                                    == JOptionPane.YES_OPTION);
                    }
                    confirm = JOptionPane.showConfirmDialog(getActionButton(), 
                                "Please confirm that the details of the student" 
                                + " that will be added are correct.", 
                                "Confirm Adding Student", 
                                JOptionPane.YES_NO_OPTION) 
                                == JOptionPane.YES_OPTION;
                    if(confirm){
                        mTable.getSTM().addData(data);
                        JOptionPane.showMessageDialog(getActionButton(), "Student added successfully!");
                        getDataDialog().dispose();
                    }

                } catch(EmptyInputException | NullPointerExceptionWithWindow | ExistingIDException e) {
                    e.printStackTrace();
                    e.startErrorWindow(getActionButton());
                } catch (SQLException e) {
                    e.printStackTrace();
                } 
            }
        });

        //Adding Components
        
        this.getGBC().gridy = 1; this.getGBC().fill = GridBagConstraints.HORIZONTAL;
        this.getDataDialog().add(this.getActionButton(),this.getGBC());
    }
}
