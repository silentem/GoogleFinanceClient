<RegEx Name="RegEx1" TriggerInsertStatement="1" Parent=""  ParentMatch="0">
  <RegularExpression>([0&amp;shy;9]{1})\t(.*)</RegularExpression>
  <InsertStatement>INSERT INTO industru(id, name)
VALUES($1, $2);</InsertStatement>
</RegEx>
