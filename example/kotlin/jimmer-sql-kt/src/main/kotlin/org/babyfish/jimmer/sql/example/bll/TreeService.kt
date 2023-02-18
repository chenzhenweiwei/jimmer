package org.babyfish.jimmer.sql.example.bll

import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.example.dal.TreeNodeRepository
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.example.model.TreeNode
import org.babyfish.jimmer.sql.example.model.by
import org.babyfish.jimmer.sql.example.model.input.RecursiveTreeInput
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * A real project should be a three-tier architecture consisting
 * of repository, service, and controller.
 *
 * This demo has no business logic, its purpose is only to tell users
 * how to use jimmer with the <b>least</b> code. Therefore, this demo
 * does not follow this convention, and let services be directly
 * decorated by `@RestController`, not `@Service`.
 */
@RestController
@RequestMapping("/tree")
@Transactional
class TreeService(
    private val treeNodeRepository: TreeNodeRepository
) {

    @Transactional
    @GetMapping("/roots/recursive")
    fun findRootTrees(
        @RequestParam(required = false) rootName: String?
    ): List<@FetchBy("RECURSIVE_FETCHER") TreeNode> =
        treeNodeRepository.findByParentIsNullAndName(rootName, RECURSIVE_FETCHER)

    @PutMapping("/root/recursive")
    fun saveTree(
        @RequestBody input: RecursiveTreeInput
    ): TreeNode {
        val treeNode = new(TreeNode::class).by(
            input.toEntity()
        ) {
            // parent must be loaded because it is a part of key
            parent = null
        }
        return treeNodeRepository.save(treeNode)
    }

    @DeleteMapping("/tree/{id}")
    fun deleteTree(@PathVariable id: Long) {
        treeNodeRepository.deleteById(id)
    }

    companion object {

        private val RECURSIVE_FETCHER = newFetcher(TreeNode::class).by {
            allScalarFields()
            childNodes({
                recursive()
            }) {
                allScalarFields()
            }
        }

        private val LOGGER = LoggerFactory.getLogger(TreeService::class.java)
    }
}