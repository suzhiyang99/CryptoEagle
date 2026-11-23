package org.apache.tika.metadata;

public interface AccessPermissions {
   String PREFIX = "access_permission:";
   Property CAN_MODIFY = Property.externalTextBag("access_permission:can_modify");
   Property EXTRACT_CONTENT = Property.externalText("access_permission:extract_content");
   Property EXTRACT_FOR_ACCESSIBILITY = Property.externalText("access_permission:extract_for_accessibility");
   Property ASSEMBLE_DOCUMENT = Property.externalText("access_permission:assemble_document");
   Property FILL_IN_FORM = Property.externalText("access_permission:fill_in_form");
   Property CAN_MODIFY_ANNOTATIONS = Property.externalText("access_permission:modify_annotations");
   Property CAN_PRINT = Property.externalText("access_permission:can_print");
   Property CAN_PRINT_DEGRADED = Property.externalText("access_permission:can_print_degraded");
}
