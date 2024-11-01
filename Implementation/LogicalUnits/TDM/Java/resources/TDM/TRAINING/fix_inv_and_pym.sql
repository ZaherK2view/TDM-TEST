-- Backup payment and invoice tables

create table invoice_bck as select * from invoice;

create table payment_bck as select * from payment;


-- Update invoice - set the amount to be bigger than zero (between 10 to 1000) when the invoice amount is zero
 update invoice set balance = (SELECT floor(random() * 1000 + 10)::int)
 where balance = 0; 
 
 
 -- update invoice - fix the due date if it is early than the issued date
update invoice
set issued_date = due_Date - interval '14 days' 
where issued_date > due_Date; 
 
  -- Update payment - align the payment amount with the invoice amount on closed invoices
 update payment 
 set amount = (select inv.balance from invoice inv where inv.invoice_id = payment.invoice_id and inv.status = 'Closed' ) 
 where payment.invoice_id in (select inv.invoice_id from invoice inv where inv.status = 'Closed'); --8112
 
  -- Update payment - set an amount that is smaller than the invoice amount if on open or new invoices
 update payment 
  set amount = (SELECT floor(random() * (select inv.balance from invoice inv where inv.invoice_id = payment.invoice_id) + 1)::int)
where payment.invoice_id in (select inv.invoice_id from invoice inv where inv.status <> 'Closed'); --16393


-- update payment - fix the date if the payment date is earlier than the invoice due date
update payment 
set issued_date = (Select due_date from invoice inv where inv.invoice_id = payment.invoice_id) 
where payment.issued_date < (select inv.due_date from invoice inv where inv.invoice_id = payment.invoice_id); --454
