package eu.toloka.tradre.analyzer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Viktor Korovyanko <viktor.koro@gmail.com>
 */
public class StockWindow {

    private Set<String> stockSet = new HashSet<>();
    private Display display;

    class CO implements ClipboardOwner {

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable transferable) {

        }
    }

    public StockWindow() {

        new Thread(new Runnable() {

            public void run() {
                display = new Display();
                Shell shell = new Shell(display, SWT.ON_TOP);
                shell.setLayout(new FillLayout());
                shell.setSize(50, 200);
                shell.setLocation(480, 280);

                final List multi = new List(shell, SWT.V_SCROLL);

                multi.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        String[] selectedItems = multi.getSelection();
                        multi.remove(multi.getSelectionIndex());

                        if(selectedItems.length == 0){
                            return;
                        }

                        CO co = new CO();

                        StringSelection stringSelection = new StringSelection(selectedItems[0]);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, co);
                    }
                });

//                shell.open();



                while (!shell.isDisposed()) {

                    if(stockSet.size() > 0){
                        java.util.List<String> items = Arrays.asList(multi.getItems());

                        for(String symbol: stockSet){
                            if(items.contains(symbol)){
                                continue;
                            }

                            multi.add(symbol, 0);
                        }

                        stockSet.clear();
                    }

                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }

                display.dispose();
            }
        }).start();
    }

    public void addSymbol(String symbol){
        stockSet.add(symbol);
        display.wake();
    }
}
