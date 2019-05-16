
(import java.util.Iterator)
(import javafx.scene.control.ListView)

;This is the class which contains retrieved rules in java
(import application.DataController)
;The class to select rules and send to jess
(import application.WekaRulesController)

(bind ?selected (get-member WekaRulesController selected))


(printout t "the following are selected is" ?selected crlf)

;Get the rules
(bind ?rules (call DataController getRules))
;			for (RipperRule r : DataController.getRules()) {

;Get iterator for the ArrayList
(if (?rules equals nil) then
   	   (printout t "JESS can't not find rules" crlf)
 else    	 
   	(printout t "JESS found rules")
   	(bind ?iterator (?rules listIterator))
   	(while (?iterator hasNext) do
   			
   		   	(printout t (?iterator next) crlf)
   		
   	)
   	
   	
	   	
   	
   	
   	   )
;	(while (?iterator hasNext)
;	   (printout t "Tinsae")


;(while (> ?n 0) do
;(definstance HeatPump
;(bind ?n (- ?n 1)))





;Get selected indices
;(bind ?lv (call 




;(bind ?jsize (?jrules size))
;(printout t "size is: " ?jsize)


;(bind ?size (call ?jrules size))
;(printout t "the size is" ?size crlf)

(assert (human Socrates))


(defrule change-baby-if-wet
"If baby is wet, change its diaper."
?wet <- (baby-is-wet)
=>
(change-baby)
(retract ?wet))