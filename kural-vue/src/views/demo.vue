<template>
 
  <div style="height: 100%;width: 100%">
    <CodeEditor @change="codeChange" v-model="code" language="go"></CodeEditor>
  </div>
</template>
 
<script>
 
import CodeEditor from "@/components/editor.vue";
 
export default {
  components: {CodeEditor},
  data() {
    return {
      code:"type user struct"
    };
  },
  created() {
 
  },
  mounted() {
 
  },
  methods: {
    codeChange(e){
      console.log(e)
    },
    
  },
};
</script>