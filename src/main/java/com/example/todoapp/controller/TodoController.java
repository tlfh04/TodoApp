package com.example.todoapp.controller;

import com.example.todoapp.dto.TodoDto;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/todos")
public class TodoController {
    //    private final TodoRepository todoRepository = new TodoRepository();
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String todos(Model model){
        // 이전에 만들었던 respository와 다른객체를 사용하면 안됨
        // TodoRepository todoRepository = new TodoRepository();
        List<TodoDto> todos = todoService.getAllTodos();

        model.addAttribute("allCount",todoService.getTotalCount());
        model.addAttribute("completedCount",todoService.getCompletedCount());
        model.addAttribute("noCompletedCount",todoService.getActiveCount());
        model.addAttribute("todos", todos);
        return "todos";
    }


    @GetMapping("/new")
    public String newTodo(Model model){
        model.addAttribute("todo",new TodoDto());
        return "new";
    }

    @PostMapping
    public String create(
            @ModelAttribute TodoDto todo,
            RedirectAttributes redirectAttributes
    ){
//        TodoDto todoDto = new TodoDto(null, title, content, false);
//        TodoRepository todoRepository = new TodoRepository();
//        TodoDto todo = todoRepository.save(todoDto);
        try{
            todoService.createTodo(todo);
            redirectAttributes.addFlashAttribute("massage","할 일이 생성되었습니다.");
            return "redirect:/todos";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
            return "redirect:/todos/new";
        }

//        return "create";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
//        TodoDto todo = todoRepository.findById(id);

        try {
            TodoDto todo = todoService.getTodoById(id);
//                    .orElseThrow(() -> new IllegalArgumentException("todo not found!!!!!!"));

            model.addAttribute("todo", todo);
            return "detail";

        } catch (IllegalArgumentException e) {
            return "redirect:/todos";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // 삭제로직
        todoService.deleteTodoById(id);
        redirectAttributes.addFlashAttribute("massage","정상적으로 삭제되었습니다.");
        redirectAttributes.addFlashAttribute("status", "delete");
        return "redirect:/todos";
    }

    @GetMapping("/{id}/update")
    public String edit(@PathVariable Long id, Model model) {
        try {
            TodoDto todo = todoService.getTodoById(id);
//                    .orElseThrow(() -> new IllegalArgumentException("todo not found!!!!!!!!"));
            model.addAttribute("todo", todo);
            return "update";
        } catch (IllegalArgumentException e) {
            return "redirect:/todos";
        }
    }

    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @ModelAttribute TodoDto todo,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            todoService.updateTodoById(id, todo);
            redirectAttributes.addFlashAttribute("massage","정상적으로 수정되었습니다.");
            return "redirect:/todos/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "없는 할일입니다.");
            return "redirect:/todos";
        }

    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<TodoDto> todos = todoService.searchTodos(keyword);

        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/active")
    public String active(Model model) {
        List<TodoDto> todos = todoService.getTodosByCompleted(false);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/completed")
    public String completed(Model model) {
        List<TodoDto> todos = todoService.getTodosByCompleted(false);
        model.addAttribute("todos", todos);
        return "todos";
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, Model model) {
        try {
            todoService.toggleCompleted(id);
            return "redirect:/todos/" + id;

        } catch (IllegalArgumentException e) {
            return "redirect:/todos";
        }
    }
    @GetMapping("/delete-completed")
    public String deleteCompleted(RedirectAttributes redirectAttributes){
//        List<TodoDto> completed = todoService.getTodosByCompleted(true);
//        for (int i = 0; i < completed.size(); i++) {
//            todoService.deleteTodoById(completed.get(i).getId());
//        }
        todoService.deleteCompletedTodos();
        redirectAttributes.addFlashAttribute("message","완료된 할일 삭제");
        return "redirect:/todos";
    }
}
