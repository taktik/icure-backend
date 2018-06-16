# Contacts, Subcontacts, Forms and Services

The following examples explains how a service is displayed given a series of Contacts

The notation ```C1[S1 -> <He1:He2-Frm1>]``` means :
The contact C1 contains a service S1 that belongs to two SubContacts : one linked to He1 (with no information about the input form used to set the value) and another linked to He2 (input form Frm1 has been used).

## Example 1: Basic sequence

```C1[S1 -> <Frm1>]```: Service S1 is created in contact C1 using input form Frm1

```C2[S1 -> <He1-Frm1>```: Service S1 is subsequently attached (during contact C2) to Healtcare Element He1 

## Example 2: Multiple associations, dissociations

```C3[S1 -> <He1-Frm1:He2>```: Service S1 is attached (during contact C3) to Healtcare Element He2 

```C4[S1 -> <He1-Frm1:He2>```: Service S1 is attached (during contact C4) to Healtcare Element He2 

```C5[S1 -> <He2>```: Service S1 is detached (during contact C5) from Healtcare Element He1 (it won't be associated from now on to He1)

## Example 3: Deletions

```C1[S1 -> <Frm1>]```: Service S1 is created in contact C1 using input form Frm1

```C2[S1 -> <He1-Frm1>```: Service S1 is subsequently attached (during contact C2) to Healtcare Element He1 

```C3[S1x]```: Service S1 is deleted in contact C3.
