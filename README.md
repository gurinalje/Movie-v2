#  在线电影购票系统 (AI-Agent Refactor & Review Benchmark)

![Vue](https://img.shields.io/badge/Vue.js-3.0-brightgreen.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Java-blue.svg)
![AI Agent](https://img.shields.io/badge/AI%20Agent-Code%20Review-purple.svg)
![Status](https://img.shields.io/badge/Status-Token%20Testing%20%26%20Tuning-orange)

##  项目背景与核心诉求 (Project Overview)

本项目的基础盘是一个基于 `Vue3 + Vite` 和 `Java Spring Boot` 的在线电影购票系统。然而，**本仓库当前的核心使命并非单纯的业务开发，而是作为一个高复杂度的“AI 自动化代码重构”与“多 Agent 协同 Code Review”的实验基准（Benchmark）项目。**

在项目初期，由于历史遗留问题，前后端代码高度耦合、目录结构极度混乱。我们目前正使用 **OpenCode** 及其底层大模型能力对项目进行深度重构。

** 【重要说明】**
由于本项目正处于 **Agent Code Review 系统** 的深度调优阶段，包含大量的长文本上下文输入、AST（抽象语法树）解析对照、以及多 Agent 之间的多轮 Review 辩论（Debate），因此**需要极高的 Token 吞吐量和超长上下文窗口支持**。这也是本阶段进行 API Token 扩容申请的主要原因。

---

##  AI Agent 与重构架构 (AI Architecture)

本项目不仅仅是简单的“代码补全”，而是基于 OpenCode 构建了一套完整的自动化代码生命周期管理链路：

### 1. 结构级重构 (Phase 1 - 已完成)
- **杂乱单体到 Monorepo 物理隔离**：利用 LLM 的语义理解能力，将原本混杂在根目录的前后端源码、配置文件、构建脚本进行精准识别与剥离。
- **构建标准化**：自动归档 Maven 标准目录树 (`src/main/java`) 与前端 Vite 资产目录。

### 2. 多 Agent 协同 Code Review (Phase 2 - Token 密集消耗阶段)
我们构建了基于大模型的 Multi-Agent 审查网络，这也是当前消耗 Token 最庞大的环节：
-  **Reviewer Agent (审查者)**：负责扫描 `movie-backend` 中的冗余业务逻辑（`blImpl`）和不规范的持久层操作（`po/data`）。
-  **Developer Agent (开发者)**：接收审查建议，自动生成重构后的 Clean Code。
-  **Critic Agent (仲裁者)**：对重构前后的代码进行安全性、性能和时空复杂度的双向对比，进行多轮思维链（Chain of Thought）论证。

### 3. OpenCode 深度调优与 Prompt 优化
- 针对 Java 复杂业务逻辑与 Vue3 Composition API 进行 Prompt Engineering 调优。
- 测试大模型在面对跨文件引用（如 `.vue` 组件调用 `api/`，再链路到后端的 `Controller`）时的上下文记忆与推理衰减情况。

---

##  为什么我们需要海量的 Token？(Token Usage Justification)

为了让 Agent 输出工业级的重构代码，传统的短文本生成已无法满足需求。我们的 Token 消耗主要集中在以下三个极端场景：

1. **全链路上下文注入 (Full-Context Injection)**
   在进行跨层重构（例如修改数据库字段，需要联动修改 `PO` -> `Mapper.xml` -> `Service` -> `Controller` -> `前端 Axios 请求` -> `Vue 视图`）时，我们必须将完整的调用链代码片段同时塞入 Prompt 中，单次请求的 Context 极易突破数万 Token。
2. **多轮博弈机制 (Multi-turn Agent Debate)**
   为了确保代码质量，我们的 Agent 被设计为具有“辩论机制”。生成一段重构代码通常需要经过 `生成 -> 审查 -> 驳回 -> 修正 -> 确认` 至少 3 到 5 轮的内部循环，Token 消耗呈指数级上升。
3. **海量样本的并行调优 (Parallel Prompt Tuning)**
   我们正在测试不同温度参数（Temperature）和系统提示词（System Prompt）对 OpenCode 代码规范性的影响，需要针对同一段历史遗留代码发起数十次并发测试以评估模型表现。

---

##  技术栈 (Tech Stack)

### 基础业务栈
- **Frontend**: Vue 3, Vite, Axios, Pinia / Vuex, Element-Plus
- **Backend**: Java, Spring Boot, MyBatis, MySQL
- **Architecture**: Monorepo (前后端物理隔离结构)

### AI 实验栈
- **Core Engine**: OpenCode / Base LLM
- **Agent Framework**: 自研多角色协同脚本 (Python / Node.js)
- **Evaluation**: Token 效能监控, AST 语法树结构比对

---

##  后续演进路线 (Roadmap)

- [x] **Milestone 1**: 乱序代码仓库的自动化物理隔离与标准化。
- [ ] **Milestone 2**: 获取充足 Token 额度，全面启动后端 `Service` 层烂代码的重构。
- [ ] **Milestone 3**: 跑通第一个基于完整业务闭环的 Agent Code Review 流程。
- [ ] **Milestone 4**: 输出《大型旧系统 AI 自动化重构实践白皮书》及效能（Token/质量）评估报告。

---
*本项目代码仅供 AI 调优与测试使用。*
