[#ftl]
<td class="search-item"><label for="${tag.id}">${tag.label!}:</label><input type="text" id="${tag.id}" name="${tag.name}" maxlength="${tag.maxlength}" [#if !tag.parameters['value']??]value="${Parameters[tag.name]!?html}"[/#if]${tag.parameterString}/></td>
