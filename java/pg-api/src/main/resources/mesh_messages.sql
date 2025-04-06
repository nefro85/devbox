SELECT * FROM public.mesh_text_message tm
WHERE
    (1 = #{all_nodes} OR tm.from_node = #{from_node})
    AND (1 = #{any_chann} OR tm.channel = #{channel})
ORDER BY text_time desc