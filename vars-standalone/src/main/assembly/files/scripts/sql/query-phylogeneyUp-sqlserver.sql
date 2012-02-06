WITH org_name AS 
    (
        SELECT DISTINCT
            parent.id AS parent_id,
            parentname.ConceptName as parent_name,
            child.id AS child_id,
            childname.ConceptName as child_name
        FROM
            Concept parent RIGHT OUTER JOIN 
            Concept child ON child.ParentConceptID_FK = parent.id LEFT OUTER JOIN
            ConceptName childname ON childname.ConceptID_FK = child.id LEFT OUTER JOIN
            ConceptName parentname ON parentname.ConceptID_FK = parent.id
        WHERE
            childname.NameType = 'Primary' AND
            parentname.NameType = 'Primary'
    ), 
    jn AS 
    (   
        SELECT
            parent_id,
            parent_name,
            child_id,
            child_name
        FROM
            org_name 
        WHERE
            child_name = 'Aegina'
        UNION ALL 
            SELECT
                C.parent_id,
                C.parent_name,
                C.child_id,
                C.child_name 
            FROM
                jn AS p JOIN 
                org_name AS C ON C.child_id = p.parent_id
    ) 
SELECT DISTINCT
    jn.parent_id,
    jn.parent_name,
    jn.child_id,
    jn.child_name
FROM
    jn 
ORDER BY
    1;